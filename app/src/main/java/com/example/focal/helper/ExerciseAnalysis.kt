package com.example.focal.helper

import android.util.Log
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2
import kotlin.math.sqrt

class ExerciseAnalysis(var jointType: String, var exercise: String) {

    fun getJointLandmarks(pose : Pose) : HashMap<String,PoseLandmark?> {
        when(jointType){
            "knee" -> {
                return hashMapOf(
                    "left ankle" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE),
                    "right ankle" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE),
                    "left knee" to pose.getPoseLandmark(PoseLandmark.LEFT_KNEE),
                    "right knee" to pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE),
                    "left hip" to pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    "right hip" to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                )
            }
            "shoulder" -> {
                return hashMapOf(
                    "left ankle" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE),
                    "right ankle" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                )
            }
            "elbow" -> {
                return hashMapOf(
                    "left ankle" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE),
                    "right ankle" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                )
            }
            else ->{
                Log.e("ExerciseAnalysis","Something went wrong in the getJointLandmarks method")
                return hashMapOf()
            }
        }
    }

    fun getFeedbackLandmarks(pose : Pose) : HashMap<String,PoseLandmark?> {
        when(exercise){
            "squat" -> {
                return hashMapOf(
                    "left ankle" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE),
                    "right ankle" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                    "left hip" to pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    "left knee" to pose.getPoseLandmark(PoseLandmark.LEFT_KNEE),
                    "right knee" to pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
                )
            }
            "shoulder press" -> {
                return hashMapOf(
                    "left ankle" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE),
                    "right ankle" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                )
            }
            "bicep curl" -> {
                return hashMapOf(
                    "left ankle" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE),
                    "right ankle" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                )
            }
            else ->{
                Log.e("ExerciseAnalysis","Something went wrong in the getFeedbackLandmarks method")
                return hashMapOf()
            }
        }
    }

    fun checkExercise(jointAngle: Double): Boolean{
        when(exercise){
            "squat" -> {
                return jointAngle in 20.0..100.0
            }
            "shoulder press" -> {
                return jointAngle in 20.0..100.0
            }
            "bicep curl" -> {
                return jointAngle in 20.0..100.0
            }
            else -> {
                Log.e("ExerciseAnalysis","No valid exercise for angle check")
                return false
            }
        }
    }

    companion object {
        @JvmStatic
        fun getDistanceBetweenPoints(x1: Float, x2: Float, y1: Float, y2: Float): Float {
            return sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        }
        @JvmStatic
        fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastPoint: PoseLandmark): Double {
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
        @JvmStatic
        fun getAngle(firstPoint: PoseLandmark, midPoint: PoseLandmark, lastX : Float, lastY : Float): Double {
            var result = Math.toDegrees(
                (atan2(lastY - midPoint.position.y,
                    lastX - midPoint.position.x)
                        - atan2(firstPoint.position.y - midPoint.position.y,
                    firstPoint.position.x - midPoint.position.x)).toDouble()
            )
            result = Math.abs(result) // Angle should never be negative
            if (result > 180) {
                result = 360.0 - result // Always get the acute representation of the angle
            }
            return result
        }
    }
}