package com.example.focal

import java.util.Date

class Goal(
    goalID : Int,
    userID : Int,
    exercise : String,
    goal : Float,
    current : Float,
    deadline : String,
    title : String,
    status : String
) {
    val goalID = goalID
    val userID = userID
    val exercise = exercise
    val goal = goal
    var current = current
    val deadline = deadline
    val title = title
    var status = status
    override fun toString(): String {
        return "$goalID,$userID,$exercise,$goal,$current,$deadline,$title,$status"
    }

}