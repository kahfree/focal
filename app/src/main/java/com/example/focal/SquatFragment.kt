package com.example.focal

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.focal.databinding.FragmentSquatBinding
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
import kotlin.math.atan2
import kotlin.math.sqrt

typealias LumaListener = (luma: Double) -> Unit

class SquatFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    private var TAG = "SquatFragment"
    private var _fragmentSquatBinding: FragmentSquatBinding? = null
    private val fragmentSquatBinding
        get() = _fragmentSquatBinding!!

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var graphicOverlay : GraphicOverlay? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentSquatBinding = FragmentSquatBinding.inflate(inflater, container, false)
        return fragmentSquatBinding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        objectDetectorHelper = ObjectDetectorHelper(
            context = requireContext(),
            objectDetectorListener = this)
        graphicOverlay = fragmentSquatBinding.graphicOverlay
    }

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

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases(){

        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentSquatBinding.viewFinder.display.rotation)
            .build()

        val poseOptions = PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE).build()
        val poseDetector = PoseDetection.getClient(poseOptions)

        val analysisUseCase = ImageAnalysis.Builder().build()

        analysisUseCase?.setAnalyzer(
            cameraExecutor,
            ImageAnalysis.Analyzer { image: ImageProxy ->
                try {
//                    val frameStartMs = SystemClock.elapsedRealtime()
//                    var bitmap: Bitmap? = null
//                    bitmap = bitmapBuffer
                    val imageToUse = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
                    poseDetector.process(imageToUse).continueWith { task ->
                        val pose = task.getResult()
                        processPose(pose!!)

                    }.addOnCompleteListener{ image.close()}
                }catch (e: MlKitException){
                    Log.e(TAG,"Failed to process image. Error: " + e.localizedMessage)
                }
            }
        )
/*
        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(fragmentSquatBinding.viewFinder.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .build()
            .also {
                it.setAnalyzer(cameraExecutor){ image ->
                    if (!::bitmapBuffer.isInitialized){
                        bitmapBuffer = Bitmap.createBitmap(
                            image.width,
                            image.height,
                            Bitmap.Config.ARGB_8888
                        )
                    }
//                    detectObjects(image)
                    val inputImage = image.image?.let { it1 -> InputImage.fromMediaImage(it1,image.imageInfo.rotationDegrees) }
                    if (inputImage != null) {
                        poseDetector.process(inputImage).addOnSuccessListener {
                            Log.e(TAG,"Success")
                            processPose(it,bitmapBuffer)
                        }.addOnFailureListener{
                            Log.e(TAG,"Failure: ${it.message}")
                        }
                    }
                    image.close()
                }
            }
*/
        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisUseCase)

            preview?.setSurfaceProvider(fragmentSquatBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectObjects(image: ImageProxy) {
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        val imageRotation = image.imageInfo.rotationDegrees
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
    }

    private fun processPose(pose: Pose, bitmap: Bitmap? = null){
        try{
            val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

            val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)

            val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

            val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
            val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)


            val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)


            val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

            val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
            val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)

            // Get confidence levels
            val lEyeConf = leftEye?.inFrameLikelihood
            val rEyeConf = rightEye?.inFrameLikelihood
            val lElbowConf = leftElbow?.inFrameLikelihood
            val rElbowConf = rightElbow?.inFrameLikelihood


            Log.e(TAG,"**CONFIDENCE LEVELS OF LANDMARKS**\nEyes: $lEyeConf $rEyeConf\nElbows: $lElbowConf $rElbowConf")

            val graphicOverlay = graphicOverlay!!
            graphicOverlay.clear()

            //Get angles of each joint
            val leftElbowAngle = getAngle(leftWrist!!,leftElbow!!,leftShoulder!!)
            val rightElbowAngle = getAngle(rightWrist!!,rightElbow!!, rightShoulder!!)
            val leftKneeAngle = getAngle(leftAnkle!!, leftKnee!!, leftHip!!)
            val rightKneeAngle = getAngle(rightAnkle!!, rightKnee!!, rightHip!!)
            val leftHipAngle = getAngle(leftKnee!!,leftHip!!,leftShoulder!!)
            val rightHipAngle = getAngle(rightKnee!!,rightHip!!,rightShoulder!!)

            //Get average values between both joints
            val avgElbowAngle = (leftElbowAngle + rightElbowAngle) / 2
            val avgKneeAngle = (leftKneeAngle + rightKneeAngle) / 2
            val avgHipAngle = (leftHipAngle + rightHipAngle) / 2

            //Log average values to console
            Log.e(TAG,"Angle of elbows: $avgElbowAngle" )
            Log.e(TAG, "Angle of knees: $avgKneeAngle")
            Log.e(TAG, "Angle of hips: $avgHipAngle")

            //Get textview's for labels
            var eyes_textview = fragmentSquatBinding.textEyesValue
            var elbows_textview = fragmentSquatBinding.textElbowsValue

            eyes_textview.setTextColor(Color.GREEN)
            //Print new values onto labels
            activity?.runOnUiThread {
                if(checkSquat(avgKneeAngle)) {
                    checkForm(pose)
                    eyes_textview.setTextColor(Color.GREEN)
                }
                else {
                    eyes_textview.setTextColor(Color.RED)
                }

                eyes_textview.text = String.format("%.1f", avgKneeAngle)
                elbows_textview.text = String.format("%.1f", avgHipAngle)
            }
        }catch (e: Exception) {
            Toast.makeText(
                activity?.baseContext,
                "Pose Landmarks failed.",
                Toast.LENGTH_SHORT).show()
            Log.d(TAG, e.localizedMessage)
        }
    }

    private fun checkForm(pose : Pose){
        val feedback : MutableList<String> = mutableListOf()
        //Check if feet are shoulder-width apart -> "Feet should be shoulder-width or slightly further apart"
        //Get the landmarks needed
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        var shoulderDistance = 0f
        var feetDistance = 0f
        //If the distance between ankles is the same or greater than the shoulder distance its all good
        if(leftAnkle != null && rightAnkle != null && leftShoulder != null && rightShoulder != null) {
            shoulderDistance = getDistanceBetweenPoints(leftShoulder.position.x,rightShoulder.position.x,leftShoulder.position.y,rightShoulder.position.y)
            feetDistance = getDistanceBetweenPoints(leftAnkle.position.x,rightAnkle.position.x,leftAnkle.position.y,rightAnkle.position.y)
            if(feetDistance < shoulderDistance)
                feedback.add("Feet should be shoulder-width or slightly further apart")
        }
        //Keep elbows at or behind your back when barbell squatting -> "Bring elbows to at or behind your back"
        //Not to sure if this is necessary nor how to implement it

        //Knees should be the same or greater distance apart than the feet -> "Knees go forwards or out, never in"
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        var kneeDistance = 0f
        if(leftKnee != null && rightKnee != null && feetDistance != 0f){
            kneeDistance = getDistanceBetweenPoints(leftKnee.position.x,rightKnee.position.x,leftKnee.position.y,rightKnee.position.y)
            if(kneeDistance < feetDistance)
                feedback.add("Knees go forwards or out, don't bring them together")
        }
        feedback.forEach{
            Log.e(TAG, it)
            Toast.makeText(
                activity?.baseContext,
                "Feedback: $it",
                Toast.LENGTH_SHORT).show()
        }
        if(feedback.size == 0)
            Toast.makeText(activity?.baseContext,"Good Squat!",Toast.LENGTH_SHORT).show()
    }

    private fun checkSquat(kneeAngle: Double): Boolean {
        if (kneeAngle in 20.0..100.0)
            return true
        return false
    }
    private fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
        var result = Math.toDegrees(
            (atan2(lastPoint.position.y - midPoint.position.y,
                lastPoint.position.x - midPoint.position.x)
                    - atan2(firstPoint.position.y - midPoint.position.y,
                firstPoint.position.x - midPoint.position.x)).toDouble()
        )
        result = Math.abs(result) // Angle should never be negative
        if (result > 180) {
            result = 360.0 - result // Always get the acute representation of the angle
        }
        return result
    }

    private fun getDistanceBetweenPoints(x1: Float, x2: Float, y1: Float, y2: Float) : Float {
        return sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
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

    override fun onInitialized() {
        objectDetectorHelper.setupObjectDetector()
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Wait for the views to be properly laid out
        fragmentSquatBinding.viewFinder.post {
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
    }

    override fun onError(error: String) {
        activity?.runOnUiThread{
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        activity?.runOnUiThread{
                fragmentSquatBinding.overlay.setResults(
                    results ?: LinkedList<Detection>(),
                    imageHeight,
                    imageWidth
                )

            fragmentSquatBinding.overlay.invalidate()
        }
    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }
}