package com.example.focal

import androidx.camera.core.ImageProxy
import java.sql.Timestamp
import java.time.LocalDateTime

class Attempt(
    exercise: String,
    statOne: Float,
    statTwo: Float,
    feedback: String
){
    val exercise = exercise
    val statOne = statOne
    val statTwo = statTwo
    val feedback = feedback
    override fun toString(): String {
        return "$exercise,$statOne,$statTwo,$feedback"
    }

    fun display(): String{
        return "\nDepth: $statOne Quality: $statTwo\nTips: $feedback"
    }

}