package com.example.focal.helper

import android.util.Log
import com.example.focal.enums.Exercises
import com.example.focal.enums.Joints
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2
import kotlin.math.sqrt

class ExerciseAnalysis(var jointType: Joints, var exercise: Exercises) {

    fun getJointLandmarks(pose : Pose) : HashMap<String,PoseLandmark?> {
        when(jointType){
            Joints.KNEE -> {
                return hashMapOf(
                    "left ankle" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE),
                    "right ankle" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE),
                    "left knee" to pose.getPoseLandmark(PoseLandmark.LEFT_KNEE),
                    "right knee" to pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE),
                    "left hip" to pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    "right hip" to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                )
            }
            Joints.SHOULDER -> {
                return hashMapOf(
                    "left elbow" to pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                    "right elbow" to pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                    "left hip" to pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    "right hip" to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                )
            }
            Joints.ELBOW -> {
                return hashMapOf(
                    "left wrist" to pose.getPoseLandmark(PoseLandmark.LEFT_WRIST),
                    "right wrist" to pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                    "left elbow" to pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                    "right elbow" to pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
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
            Exercises.SQUAT -> {
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
            Exercises.SHOULDER_PRESS -> {
                return hashMapOf(
                    "left elbow" to pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                    "right elbow" to pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                    "left wrist" to pose.getPoseLandmark(PoseLandmark.LEFT_WRIST),
                    "right wrist" to pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST),
                    "left hip" to pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    "right hip" to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                )
            }
            Exercises.BICEP_CURL -> {
                return hashMapOf(
                    "left elbow" to pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW),
                    "right elbow" to pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW),
                    "left shoulder" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER),
                    "right shoulder" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER),
                    "left hip" to pose.getPoseLandmark(PoseLandmark.LEFT_HIP),
                    "right hip" to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                )
            }
            else ->{
                Log.e("ExerciseAnalysis","Something went wrong in the getFeedbackLandmarks method")
                return hashMapOf()
            }
        }
    }

    fun getAverageJointAngle(pose: Pose) : Double{
        when(jointType){
            Joints.ELBOW -> {
                val jointLandmarks = this.getJointLandmarks(pose)

                //Get angles of each joint
                val leftElbowAngle = ExerciseAnalysis.getAngle(jointLandmarks["left shoulder"]!!, jointLandmarks["left elbow"]!!, jointLandmarks["left wrist"]!!)
                val rightElbowAngle = ExerciseAnalysis.getAngle(jointLandmarks["right shoulder"]!!, jointLandmarks["right elbow"]!!, jointLandmarks["right wrist"]!!)
                //Get average values between both joints
                return (leftElbowAngle + rightElbowAngle) / 2
            }
            Joints.SHOULDER -> {
                val jointLandmarks = this.getJointLandmarks(pose)

                //Get angles of each joint
                val leftArmpitAngle = ExerciseAnalysis.getAngle(jointLandmarks["left elbow"]!!, jointLandmarks["left shoulder"]!!, jointLandmarks["left hip"]!!)
                val rightArmpitAngle = ExerciseAnalysis.getAngle(jointLandmarks["right elbow"]!!, jointLandmarks["right shoulder"]!!, jointLandmarks["right hip"]!!)

                //Get average values between both joints
                return (leftArmpitAngle + rightArmpitAngle) / 2
            }
            Joints.KNEE -> {
                val landmarkList = this.getJointLandmarks(pose)

                //Get angles of each joint
                val LkneeAngle = ExerciseAnalysis.getAngle(landmarkList["left ankle"]!!,landmarkList["left knee"]!!,landmarkList["left hip"]!!)
                val RkneeAngle = ExerciseAnalysis.getAngle(landmarkList["right ankle"]!!,landmarkList["right knee"]!!,landmarkList["right hip"]!!)

                return (LkneeAngle + RkneeAngle) / 2
            }
            else ->{
                Log.e("ExerciseAnalysis","Something went wrong in the getAverageJointAngle method")
                return 0.0
            }
        }
    }

    fun checkExercise(jointAngle: Double): Boolean{
        when(exercise){
            Exercises.SQUAT -> {
                return jointAngle in 20.0..100.0
            }
            Exercises.SHOULDER_PRESS -> {
                return jointAngle in 45.0..180.0
            }
            Exercises.BICEP_CURL -> {
                return jointAngle in 20.0..170.0
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
            return sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1))
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