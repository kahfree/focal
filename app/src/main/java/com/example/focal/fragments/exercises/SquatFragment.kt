package com.example.focal.fragments.exercises

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.focal.R
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.time.Duration
import java.time.LocalTime

class SquatFragment : Fragment(){

    private var TAG = "SquatFragment"
    private var _fragmentSquatBinding: FragmentSquatBinding? = null
    private val fragmentSquatBinding
        get() = _fragmentSquatBinding!!
    //Variables to run exercise analysis
    private lateinit var cameraExecutor: ExecutorService
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val exerciseAnalysis : ExerciseAnalysis = ExerciseAnalysis(Joints.KNEE,Exercises.SQUAT)
    //Variables to log and track the exercise analysis
    private var timeRemaining : LocalTime? = null
    private var maxDepth : Float = 361f
    private var goodSquat : Float = 0f
    private var badSquat : Float = 0f
    private lateinit var squatFeedback : HashMap<String,String>
    private var userID: String = ""

    //Adds the binding so all UI elements can be accessed
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentSquatBinding = FragmentSquatBinding.inflate(inflater, container, false)
        return fragmentSquatBinding.root
    }
    //Initialise all variables
    @SuppressLint("MissingPermission", "NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait for the views to be properly laid out
        fragmentSquatBinding.viewFinder.post {
            // Set up the camera and its use cases
            if(allPermissionsGranted()){
                setUpCamera()
            } else {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        REQUIRED_PERMISSIONS,
                        REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        }
        timeRemaining = LocalTime.now().plusSeconds(20)
        fragmentSquatBinding.buttonDashboard.visibility = View.INVISIBLE
        squatFeedback = HashMap<String, String>()
        cameraExecutor = Executors.newSingleThreadExecutor()
        userID = requireArguments().getString("userID")!!
        maxDepth = 361f
        goodSquat = 0f
        badSquat = 0f
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
        //Setup and configure phone camera
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        //Configure camera preview window for user
        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentSquatBinding.viewFinder.display.rotation)
            .build()
        //Build and configure pose estimation model
        val poseOptions = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        val poseDetector = PoseDetection.getClient(poseOptions)
        //Setup analysis use case to run pose estimation on each frame
        val analysisUseCase = ImageAnalysis.Builder().build()
        analysisUseCase.setAnalyzer(
            cameraExecutor
        ) { image: ImageProxy ->
            try {
                // If the time is up display the post-exercise dashboard
                if (LocalTime.now().isAfter(timeRemaining)) {
                    postExerciseDashboard()
                } else {
                    // Convert the image from a CameraX 'ImageProxy' to an Google 'InputImage'
                    val imageToUse = InputImage.fromMediaImage(
                        image.image!!,
                        image.imageInfo.rotationDegrees
                    )
                    // Run inference on the input image and analyze the frame for feedback
                    poseDetector.process(imageToUse).continueWith { task ->
                        val pose = task.result
                        //Send pose result for analysis
                        processPose(pose!!)
                    }.addOnCompleteListener {
                        // Close the CameraX image so another can be inputted and prevent hanging
                        image.close()
                    }
                }
            } catch (e: MlKitException) {
                Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
            }

        }
        //Make sure no use cases are already bound
        cameraProvider.unbindAll()

        try {
            //Apply the use cases to the users camera
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisUseCase)
            //Refresh the camera preview for the user
            preview?.setSurfaceProvider(fragmentSquatBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }
    //Once the time for analysis runs out, variables are packaged up to be sent to post-exercise dashboard
    private fun postExerciseDashboard(){
        //Check if exercise was perfect quality or not
        if(badSquat == 0f)
            squatFeedback.put("Good Squat!", "No mistakes were made, keep up the good work!")
        val quality = goodSquat / (goodSquat + badSquat) * 100
        //Make the button to go to the post-exercise dashboard visible to the user
        activity?.runOnUiThread { fragmentSquatBinding.buttonDashboard.visibility = View.VISIBLE }
        //On button click open post-exercise dashboard with all the variables
        fragmentSquatBinding.buttonDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_SquatFragment_to_postExerciseDashboard, Bundle().apply {
                putFloat("maxDepth", maxDepth)
                putString("exercise", "Squat")
                putFloat("exerciseQuality", quality)
                putSerializable("feedbackToGive",squatFeedback)
                putString("userID",userID)
            })
        }

    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun processPose(pose: Pose, bitmap: Bitmap? = null){
        try{
            //Get average values between both joints
            val avgKneeAngle = exerciseAnalysis.getAverageJointAngle(pose)
            //Get textview's for labels
            val eyes_textview = fragmentSquatBinding.textEyesValue
            val timer_textview = fragmentSquatBinding.textTimerValue
            val remaining_time = Duration.between(LocalTime.now(),timeRemaining).toSecondsPart()

            eyes_textview.setTextColor(Color.GREEN)

            if(avgKneeAngle < maxDepth){
                Log.e("Max Depth", "Recording new max depth from $maxDepth to $avgKneeAngle")
                maxDepth = avgKneeAngle.toFloat()
            }
            //Print new values onto labels
            activity?.runOnUiThread {
                //If the angle falls in the right range
                if(exerciseAnalysis.checkExercise(avgKneeAngle)) {
                    //Check its form using the landmarks needed to apply feedback
                    checkForm(exerciseAnalysis.getFeedbackLandmarks(pose))
                    //Alert the user their angle is good by changing text color
                    eyes_textview.setTextColor(Color.GREEN)
                }else {
                    //Let the user know they're not in exercise range
                    fragmentSquatBinding.textFeedback.text = ""
                    eyes_textview.setTextColor(Color.RED)
                }
                //Update the values on screen
                eyes_textview.text = String.format("%.1f", avgKneeAngle)
                timer_textview.text = remaining_time.toString() + "s"
            }
        }catch (e: Exception) {
            Log.e(TAG, "Error in process pose method")
            Log.d(TAG, e.message!!)
        }
    }

    private fun checkForm(feedbackLandmark : HashMap<String,PoseLandmark?>){
        val feedback : MutableList<String> = mutableListOf()
        //Check if feet are shoulder-width apart -> "Feet should be shoulder-width or slightly further apart"
        //Get the landmarks needed
        val leftAnkle = feedbackLandmark["left ankle"]
        val rightAnkle = feedbackLandmark["right ankle"]
        val leftShoulder = feedbackLandmark["left shoulder"]
        val rightShoulder = feedbackLandmark["right shoulder"]
        val leftKnee = feedbackLandmark["left knee"]
        val rightKnee = feedbackLandmark["right knee"]
        val leftHip = feedbackLandmark["left hip"]
        val shoulderDistance: Float
        var feetDistance = 0f

        //The stance should be the same width or wider than the shoulders
        if(leftAnkle != null && rightAnkle != null && leftShoulder != null && rightShoulder != null) {
            shoulderDistance = ExerciseAnalysis.getDistanceBetweenPoints(leftShoulder.position.x,rightShoulder.position.x,leftShoulder.position.y,rightShoulder.position.y)
            feetDistance = ExerciseAnalysis.getDistanceBetweenPoints(leftAnkle.position.x,rightAnkle.position.x,leftAnkle.position.y,rightAnkle.position.y)
            if(feetDistance <= shoulderDistance) {
                feedback.add("Wider Stance!")
                squatFeedback.put(
                    "Wider Stance!",
                    "Feet should be shoulder-width or slightly further apart"
                )
            }
        }

        //Back should be between 90 - 40 degrees to the floor -> "Don't lean too far forward"
        //Get the back angle which is from the shoulder -> hip -> floor
        //Have to generate the floor angle myself, taking the x of the shoulder and the y of the hip
        if(leftShoulder != null && leftHip != null) {
            val backAngle =
                ExerciseAnalysis.getAngle(leftShoulder, leftHip, leftShoulder.position.x, leftHip.position.y)
            if(backAngle <= 40) {
                feedback.add("Lean Back!")
                squatFeedback.put(
                    "Lean back!",
                    "To prevent back injury, keep your back between 90 and 40 degrees to the floor"
                )
            }
            else if(backAngle > 90)
                feedback.add("Don't lean too far back")
        }

        //Knees should be the same or greater distance apart than the feet -> "Knees go forwards or out, never in"
        var kneeDistance = 0f
        if(leftKnee != null && rightKnee != null && feetDistance != 0f){
            kneeDistance = ExerciseAnalysis.getDistanceBetweenPoints(leftKnee.position.x,rightKnee.position.x,leftKnee.position.y,rightKnee.position.y)
            if(kneeDistance <= feetDistance) {
                feedback.add("Knees out!")
                squatFeedback.put(
                    "Knees out!",
                    "Knees go forwards or out, don't bring them together"
                )
            }
        }
        val feedbacktext = fragmentSquatBinding.textFeedback
        if(feedback.size == 0) {
            goodSquat++
            activity?.runOnUiThread {
                feedbacktext.setTextColor(Color.GREEN)
                feedbacktext.text = "Good Squat!"
            }
            return
        }

        badSquat++
        feedback.forEach{
            Log.e(TAG, it)
        }

        val message = feedback.joinToString("\n")

        activity?.runOnUiThread {
            feedbacktext.setTextColor(Color.RED)
            feedbacktext.text = message
        }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        activity?.let { it1 ->
            ContextCompat.checkSelfPermission(
                it1.baseContext, it)
        } == PackageManager.PERMISSION_GRANTED
    }
    //A companion object is where you can have static variables and methods in a class
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                //If the build version is less than or equal to the latest public release
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    //Also request the permission to write to external storage
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    //Event called when user responds to permissions request
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