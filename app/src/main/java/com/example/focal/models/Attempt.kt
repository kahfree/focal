package com.example.focal.models

import androidx.camera.core.ImageProxy
import java.sql.Timestamp
import java.time.LocalDateTime
data class Attempt(val exercise : String? = null, val statOne: Float? = null, val statTwo : Float? = null, val feedback : String? = null) {
    override fun toString(): String {
        return "Attempt(exercise=$exercise, statOne=$statOne, statTwo=$statTwo, feedback=$feedback)"
    }
    fun display(): String{
        return "Exercise: $exercise\nDepth: $statOne Quality: $statTwo\nTips: $feedback"
    }
}
//class Attempt(
//    exercise: String,
//    statOne: Float,
//    statTwo: Float,
//    feedback: String
//){
//    val exercise = exercise
//    val statOne = statOne
//    val statTwo = statTwo
//    val feedback = feedback
//    override fun toString(): String {
//        return "$exercise,$statOne,$statTwo,$feedback"
//    }
//
//    fun display(): String{
//        return "\nDepth: $statOne Quality: $statTwo\nTips: $feedback"
//    }
//
//}