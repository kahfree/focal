package com.example.focal.models

import java.util.Date
data class Goal(val goalID : String? = null, val userID: String? = null, val exercise : String? = null, val goal : Float? = null, var current: Float? = null,val deadline: String? = null, val title: String? = null,
var status: String? = null) {
    override fun toString(): String {
        return "Goal(goalID=$goalID, userID=$userID, exercise=$exercise, goal=$goal, current=$current, deadline=$deadline, title=$title, status=$status)"
    }

}
//class Goal(
//    goalID : String,
//    userID : String,
//    exercise : String,
//    goal : Float,
//    current : Float,
//    deadline : String,
//    title : String,
//    status : String
//) {
//    val goalID = goalID
//    val userID = userID
//    val exercise = exercise
//    val goal = goal
//    var current = current
//    val deadline = deadline
//    val title = title
//    var status = status
//    override fun toString(): String {
//        return "$goalID,$userID,$exercise,$goal,$current,$deadline,$title,$status"
//    }
//
//}