package com.example.focal

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.Detection
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

class ObjectDetectorHelper(
    var threshold: Float = 0.5f,
    var numThreads: Int = 2,
    var maxResults: Int = 3,
    var currentDelegate: Int = 0,
    var currentModel: Int = 0,
    val context: Context,
    val objectDetectorListener: DetectorListener
)  {

    private val TAG = "ObjectDetectionHelper"

    private var objectDetector: ObjectDetector? = null
    private var gpuSupported = false

    init {

        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder =
                TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            objectDetectorListener.onInitialized()
        }.addOnFailureListener{
            objectDetectorListener.onError("TfLiteVision failed to initialize: "
                    + it.message)
        }
    }

    fun setupObjectDetector(){
        val modelName = "mobilenetv1.tflite"
//        val modelName = "model_new_metadata.tflite"
        val optionsBuilder =
            ObjectDetector.ObjectDetectorOptions.builder()
                .setScoreThreshold(threshold)
                .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        when (currentDelegate) {
            DELEGATE_CPU -> {
                // Default
            }
            DELEGATE_GPU -> {
                if (gpuSupported) {
                    baseOptionsBuilder.useGpu()
                } else {
                    objectDetectorListener.onError("GPU is not supported on this device")
                }
            }
            DELEGATE_NNAPI -> {
                baseOptionsBuilder.useNnapi()
            }
        }

        try {
            objectDetector =
                ObjectDetector.createFromFileAndOptions(context, modelName, optionsBuilder.build())
        } catch (e: Exception) {
            objectDetectorListener.onError(
                "Object detector failed to initialize. See error logs for details"
            )
            Log.e(TAG, "TFLite failed to load model with error: " + e.message)
        }
    }

    fun detect(image: Bitmap, imageRotation: Int) {
        if(objectDetector == null) {
            setupObjectDetector()
        }

        var inferenceTime = SystemClock.uptimeMillis()
        val imageProcessor = ImageProcessor.Builder().add(Rot90Op(-imageRotation / 90)).build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(image))
        val results = objectDetector?.detect(tensorImage)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        objectDetectorListener.onResults(
            results,
            inferenceTime,
            tensorImage.height,
            tensorImage.width
        )

    }

    interface DetectorListener {
        fun onInitialized()
        fun onError(error: String)
        fun onResults(
            results: MutableList<Detection>?,
            inferenceTime: Long,
            imageHeight: Int,
            imageWidth: Int
        )
    }

    companion object {
        const val DELEGATE_CPU = 0
        const val DELEGATE_GPU = 1
        const val DELEGATE_NNAPI = 2
        const val MODEL_MOBILENETV1 = 0
        const val MODEL_EFFICIENTDETV0 = 1
        const val MODEL_EFFICIENTDETV1 = 2
        const val MODEL_EFFICIENTDETV2 = 3
    }
}