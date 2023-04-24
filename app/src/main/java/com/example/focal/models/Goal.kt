package com.example.focal.models

data class Goal(val goalID : String? = null, val userID: String? = null, val exercise : String? = null, val goal : Float? = null, var current: Float? = null,val deadline: String? = null, val title: String? = null,
var status: String? = null) {
    override fun toString(): String {
        return "Goal(goalID=$goalID, userID=$userID, exercise=$exercise, goal=$goal, current=$current, deadline=$deadline, title=$title, status=$status)"
    }

}