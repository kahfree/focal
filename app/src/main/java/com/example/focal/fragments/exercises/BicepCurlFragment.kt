package com.example.focal.fragments.exercises
//Android Imports
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
//Project Imports
import com.example.focal.R
import com.example.focal.databinding.FragmentBicepCurl2Binding
import com.example.focal.enums.Exercises
import com.example.focal.enums.Joints
import com.example.focal.helper.ExerciseAnalysis
//Pose estimation Imports
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

class BicepCurlFragment : Fragment(){

    private var TAG = "BicepCurlFragment"
    private var _fragmentBicepCurlBinding: FragmentBicepCurl2Binding? = null
    private val fragmentBicepCurlBinding
        get() = _fragmentBicepCurlBinding!!
    //Variables to run exercise analysis
    private lateinit var cameraExecutor: ExecutorService
    private var preview: Preview? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private val exerciseAnalysis: ExerciseAnalysis = ExerciseAnalysis(Joints.ELBOW,Exercises.BICEP_CURL)
    //Variables to log and track the exercise analysis
    private var timeRemaining : LocalTime? = null
    private var rangeOfMotion : Float = 361f
    private var goodCurl : Float = 0f
    private var badCurl : Float = 0f
    private var topOfMovementReached : Boolean = false
    private lateinit var curlFeedback : HashMap<String,String>
    private var userID : String = ""


    //Adds the binding so all UI elements can be accessed
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentBicepCurlBinding = FragmentBicepCurl2Binding.inflate(inflater, container, false)
        return fragmentBicepCurlBinding.root
    }
    //Initialise all variables
    @SuppressLint("MissingPermission", "NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait for the views to be properly laid out
        fragmentBicepCurlBinding.viewFinder.post {
            // Set up the camera and its use cases
            if(allPermissionsGranted()){
                setUpCamera()
            } else {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        }
        timeRemaining = LocalTime.now().plusSeconds(20)
        fragmentBicepCurlBinding.buttonDashboard.visibility = View.INVISIBLE
        curlFeedback = HashMap<String, String>()
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        userID = requireArguments().getString("userID").toString()
        rangeOfMotion = 361f
        goodCurl = 0f
        badCurl = 0f
        topOfMovementReached = false
        curlFeedback = HashMap<String,String>()
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
            .setTargetRotation(fragmentBicepCurlBinding.viewFinder.display.rotation)
            .build()
        //Build and configure pose estimation model
        val poseOptions = PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE).build()
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
            preview?.setSurfaceProvider(fragmentBicepCurlBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }
    //Once the time for analysis runs out, variables are packaged up to be sent to post-exercise dashboard
    private fun postExerciseDashboard(){
        //Check if exercise was perfect quality or not
        if(badCurl == 0f)
            curlFeedback.put("Good Bicep Curl!", "No mistakes were made, keep up the good work!")
        val quality = goodCurl / (goodCurl + badCurl) * 100
        //Make the button to go to the post-exercise dashboard visible to the user
        activity?.runOnUiThread { fragmentBicepCurlBinding.buttonDashboard.visibility = View.VISIBLE }
        //On button click open post-exercise dashboard with all the variables
        fragmentBicepCurlBinding.buttonDashboard.setOnClickListener {
            findNavController().navigate(R.id.action_bicepCurlFragment_to_postExerciseDashboard, Bundle().apply {
                putFloat("maxDepth", rangeOfMotion)
                putString("exercise", "Bicep Curl")
                putFloat("exerciseQuality", quality)
                putSerializable("feedbackToGive",curlFeedback)
                putString("userID", userID)
            })
        }

    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun processPose(pose: Pose){
        try{
            //Get average values between both joints
            val avgElbowAngle = exerciseAnalysis.getAverageJointAngle(pose)
            //Setup UI to start analysing exercise
            val elbowAngle_textview = fragmentBicepCurlBinding.textElbowAngleValue
            val timer_textview = fragmentBicepCurlBinding.textTimerValue
            val remainingTime = Duration.between(LocalTime.now(),timeRemaining).toSecondsPart()
            elbowAngle_textview.setTextColor(Color.GREEN)

            //Print new values onto labels
            activity?.runOnUiThread {
                //If the angle falls in the right range
                if(exerciseAnalysis.checkExercise(avgElbowAngle)) {
                    //Check its form using the landmarks needed to apply feedback
                    checkForm(exerciseAnalysis.getFeedbackLandmarks(pose))
                    //Alert the user their angle is good by changing text color
                    elbowAngle_textview.setTextColor(Color.GREEN)
                }
                else {
                    //Let the user know they're not in exercise range
                    fragmentBicepCurlBinding.textFeedback.text = ""
                    elbowAngle_textview.setTextColor(Color.RED)
                }
                //Update the values on screen
                elbowAngle_textview.text = String.format("%.1f", avgElbowAngle)
                timer_textview.text = remainingTime.toString() + "s"
            }
        }catch (e: Exception) {
            e.localizedMessage?.let { Log.d(TAG, it) }
        }
    }

    private fun checkForm(feedbackLandmarks : HashMap<String,PoseLandmark?>){
        val feedback : MutableList<String> = mutableListOf()
        //Get the landmarks needed
        val leftElbow = feedbackLandmarks["left elbow"]
        val rightElbow = feedbackLandmarks["right elbow"]
        val leftShoulder = feedbackLandmarks["left shoulder"]
        val rightShoulder = feedbackLandmarks["right shoulder"]
        val leftHip = feedbackLandmarks["left hip"]
        val rightHip = feedbackLandmarks["right hip"]

        //The stance should be the same width or wider than the shoulders
        if(leftElbow != null && rightElbow != null && leftShoulder != null && rightShoulder != null && leftHip != null && rightHip != null) {

            if(leftElbow.position.x - leftShoulder.position.x >= 0 && rightElbow.position.x - rightShoulder.position.x >= 0){
                feedback.add("Elbows Back!")
                curlFeedback.put(
                    "Elbows Back!",
                    "Elbows should be inline or behind shoulders"
                )
            }

            if(leftHip.position.x - leftShoulder.position.x >= 0 && rightHip.position.x - rightShoulder.position.x >= 0){
                feedback.add("Hips Back!")
                curlFeedback.put(
                    "Hips Back!",
                    "Hips should be inline or behind shoulders"
                )
            }
        }


        val feedbacktext = fragmentBicepCurlBinding.textFeedback
        if(feedback.size == 0) {
            goodCurl++
            activity?.runOnUiThread {
                feedbacktext.setTextColor(Color.GREEN)
                feedbacktext.text = "Good Bicep Curl!"
            }
            return
        }

        badCurl++
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