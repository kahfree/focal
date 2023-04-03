package com.example.focal

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focal.databinding.FragmentShoulderPressBinding
import com.example.focal.databinding.FragmentSquatBinding
import com.example.focal.enums.Exercises
import com.example.focal.enums.Joints
import com.example.focal.helper.ExerciseAnalysis
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.tensorflow.lite.task.gms.vision.detector.Detection
import java.nio.ByteBuffer
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class ShoulderPressFragment : Fragment(){

    private var TAG = "ShoulderPressFragment"
    private var _fragmentShoulderPressBinding: FragmentShoulderPressBinding? = null
    private val fragmentShoulderPressBinding
        get() = _fragmentShoulderPressBinding!!

    private lateinit var cameraExecutor: ExecutorService
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var timeRemaining : LocalTime? = null
    private var maxDepth : Float = 361f
    private var goodSquat : Float = 0f
    private var badSquat : Float = 0f
    private var topOfMovementReached : Boolean = false
    private lateinit var squatFeedback : HashMap<String,String>
    private var userID : String = ""
    private val exerciseAnalysis: ExerciseAnalysis = ExerciseAnalysis(Joints.SHOULDER,Exercises.SHOULDER_PRESS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentShoulderPressBinding = FragmentShoulderPressBinding.inflate(inflater, container, false)
        return fragmentShoulderPressBinding.root
    }

    @SuppressLint("MissingPermission", "NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait for the views to be properly laid out
        fragmentShoulderPressBinding.viewFinder.post {
            // Set up the camera and its use cases
            if(allPermissionsGranted()){
                setUpCamera()
            } else {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                }
            }
        }
        timeRemaining = LocalTime.now().plusSeconds(20)
        fragmentShoulderPressBinding.buttonDashboard.visibility = View.INVISIBLE
        squatFeedback = HashMap<String, String>()
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        userID = requireArguments().getString("userID")!!
        maxDepth = 361f
        goodSquat = 0f
        badSquat = 0f
        topOfMovementReached = false
        squatFeedback = HashMap<String,String>()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases(){

        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentShoulderPressBinding.viewFinder.display.rotation)
            .build()

        /* POTENTIAL SOLUTIONS FOR SLOW STARTUP
            1. SET PERFORMANCE MODE TO QUICK INSTEAD OF ACCURATE
            2. CLOSE THE MODEL AFTER EACH FRAME
            3. STARTUP THE MODEL ON FRAGMENT CREATION
            4. RUN MODEL SETUP ON BACKGROUND THREAD
        */
        val poseOptions = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(poseOptions)

        val analysisUseCase = ImageAnalysis.Builder().build()

        analysisUseCase.setAnalyzer(
            cameraExecutor,

            ImageAnalysis.Analyzer { image: ImageProxy ->
                try {
                    // If the time is up display the post-exercise dashboard
                    if(LocalTime.now().isAfter(timeRemaining)) {
                        postExerciseDashboard()
                    }
                    else {
                        // Convert the image from a CameraX 'ImageProxy' to an Google 'InputImage'
                        val imageToUse = InputImage.fromMediaImage(
                            image.image!!,
                            image.imageInfo.rotationDegrees
                        )

                        // Run inference on the input image and analyze the frame for feedback
                        poseDetector.process(imageToUse).continueWith { task ->
                            val pose = task.result
                            processPose(pose!!)
                        }.addOnCompleteListener {
                            // Close the CameraX image so another can be inputted and prevent hanging
                            image.close()
                        }
                    }
                }catch (e: MlKitException){
                    Log.e(TAG,"Failed to process image. Error: " + e.localizedMessage)
                }

            }
        )
        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisUseCase)
            preview?.setSurfaceProvider(fragmentShoulderPressBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun postExerciseDashboard(){
        if(badSquat == 0f)
            squatFeedback.put("Good Shoulder Press!", "No mistakes were made, keep up the good work!")
        val quality = goodSquat / (goodSquat + badSquat) * 100
        activity?.runOnUiThread { fragmentShoulderPressBinding.buttonDashboard.visibility = View.VISIBLE }

        fragmentShoulderPressBinding.buttonDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_shoulderPressFragment_to_postExerciseDashboard, Bundle().apply {
                putFloat("maxDepth", maxDepth)
                putString("exercise", "Shoulder Press")
                putFloat("exerciseQuality", quality)
                putSerializable("feedbackToGive",squatFeedback)
                putString("userID", userID)
            })
        }

    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun processPose(pose: Pose, bitmap: Bitmap? = null){
        try{

            val jointLandmarks = exerciseAnalysis.getJointLandmarks(pose)

            //Get angles of each joint
            val leftArmpitAngle = ExerciseAnalysis.getAngle(jointLandmarks["left elbow"]!!, jointLandmarks["left shoulder"]!!, jointLandmarks["left hip"]!!)
            val rightArmpitAngle = ExerciseAnalysis.getAngle(jointLandmarks["right elbow"]!!, jointLandmarks["right shoulder"]!!, jointLandmarks["right hip"]!!)

            //Get average values between both joints
            val avgArmpitAngle = (leftArmpitAngle + rightArmpitAngle) / 2

            var eyes_textview = fragmentShoulderPressBinding.textArmpitAngleValue
            var timer_textview = fragmentShoulderPressBinding.textTimerValue
            val remainingTime = Duration.between(LocalTime.now(),timeRemaining).toSecondsPart()

            eyes_textview.setTextColor(Color.GREEN)
            //Print new values onto labels
            if(topOfMovementReached && avgArmpitAngle < maxDepth) {
                Log.e("Max Depth", "Recording new max depth from $maxDepth to $avgArmpitAngle")
                maxDepth = avgArmpitAngle.toFloat()
            }
            activity?.runOnUiThread {
                if(exerciseAnalysis.checkExercise(avgArmpitAngle)) {
                    checkForm(exerciseAnalysis.getFeedbackLandmarks(pose))
                    eyes_textview.setTextColor(Color.GREEN)
                }
                else {
                    fragmentShoulderPressBinding.textFeedback.text = ""
                    eyes_textview.setTextColor(Color.RED)
                }

                eyes_textview.text = String.format("%.1f", avgArmpitAngle)
                timer_textview.text = remainingTime.toString() + "s"
            }
        }catch (e: Exception) {
            Log.d(TAG, e.localizedMessage)
        }
    }

    private fun checkForm(feedbackLandmarks : HashMap<String,PoseLandmark?>){
        val feedback : MutableList<String> = mutableListOf()
        //Check if feet are shoulder-width apart -> "Feet should be shoulder-width or slightly further apart"
        //Get the landmarks needed
        val leftElbow = feedbackLandmarks["left elbow"]
        val rightElbow = feedbackLandmarks["right elbow"]
        val leftShoulder = feedbackLandmarks["left shoulder"]
        val rightShoulder = feedbackLandmarks["right shoulder"]
        val leftWrist = feedbackLandmarks["left wrist"]
        val rightWrist = feedbackLandmarks["right wrist"]
        val leftHip = feedbackLandmarks["left hip"]
        val rightHip = feedbackLandmarks["right hip"]
        var shoulderDistance = 0f
        var elbowDistance = 0f
        var wristDistance = 0f

        //The stance should be the same width or wider than the shoulders
        if(leftElbow != null && rightElbow != null && leftShoulder != null && rightShoulder != null && leftWrist != null && rightWrist != null) {
            shoulderDistance = ExerciseAnalysis.getDistanceBetweenPoints(leftShoulder.position.x,rightShoulder.position.x,leftShoulder.position.y,rightShoulder.position.y)
            elbowDistance = ExerciseAnalysis.getDistanceBetweenPoints(leftElbow.position.x,rightElbow.position.x,leftElbow.position.y,rightElbow.position.y)
            wristDistance = ExerciseAnalysis.getDistanceBetweenPoints(leftWrist.position.x,rightWrist.position.x,leftWrist.position.y,rightWrist.position.y)
            if(elbowDistance <= shoulderDistance) {
                feedback.add("Elbows Out!")
                squatFeedback.put(
                    "Elbows Out!",
                    "Elbows should be same width or wider than shoulders"
                )
            }
            if(wristDistance <= shoulderDistance){
                feedback.add("Wider Hands!")
                squatFeedback.put(
                    "Wider Hands!",
                    "Hands should be same width or wider than shoulders"
                )
            }

            val leftArmpitAngle = ExerciseAnalysis.getAngle(leftElbow, leftShoulder, leftHip!!)
            val rightArmpitAngle = ExerciseAnalysis.getAngle(rightElbow, rightShoulder, rightHip!!)

            //Get average values between both joints
            val avgArmpitAngle = (leftArmpitAngle + rightArmpitAngle) / 2
            //This works, but only if the left arm is above the right
            //Could workaround by doing another check in statement, but there must be a maths flaw here...
            //Added the OR clause for the meantime, still doesn't feel right
            if((abs(leftWrist.position.y) - abs(rightWrist.position.y)) >= 50 || (abs(rightWrist.position.y) - abs(leftWrist.position.y)) >= 50){
                feedback.add("Both Arms!")
                squatFeedback.put(
                    "Both Arms!",
                    "Both arms should be pressed at the same time"
                )
            }
            else if(avgArmpitAngle >= 130.0){
                topOfMovementReached = true
            }
        }


        val feedbacktext = fragmentShoulderPressBinding.textFeedback
        if(feedback.size == 0) {
            goodSquat++
            activity?.runOnUiThread {
                feedbacktext.setTextColor(Color.GREEN)
                feedbacktext.text = "Good Shoulder Press!"
            }
            return
        }

        badSquat++
        feedback.forEach{
            Log.e(TAG, it)
        }

        activity?.runOnUiThread {
            feedbacktext.setTextColor(Color.RED)
            feedbacktext.text = feedback.joinToString("\n")
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        activity?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1.baseContext, it)
        } == PackageManager.PERMISSION_GRANTED
    }
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setUpCamera()
            } else {
                Toast.makeText(
                    activity?.baseContext,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }
    }
}