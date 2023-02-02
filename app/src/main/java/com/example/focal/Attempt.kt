package com.example.focal

import androidx.camera.core.ImageProxy
import java.sql.Timestamp
import java.time.LocalDateTime

class Attempt(
    attemptID: Int,
    userID: Int,
    exercise: String,
    datetime: LocalDateTime,
    statOne: Float,
    statTwo: Float,
    feedback: String
){
    val attemptID = attemptID
    val userID = userID
    val exercise = exercise
    val datetime = datetime
    val statOne = statOne
    val statTwo = statTwo
    val feedback = feedback
    override fun toString(): String {
        return "$attemptID,$userID,$exercise,$datetime,$statOne,$statTwo,$feedback"
    }

    fun display(): String{
        return "$datetime\nDepth: $statOne Quality: $statTwo\nTips: $feedback"
    }

}