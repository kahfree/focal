package com.example.focal.models

data class Attempt(val exercise : String? = null, val statOne: Float? = null, val statTwo : Float? = null, val feedback : String? = null) {
    override fun toString(): String {
        return "Attempt(exercise=$exercise, statOne=$statOne, statTwo=$statTwo, feedback=$feedback)"
    }
    fun display(): String{
        return "Exercise: $exercise\nDepth: $statOne Quality: $statTwo\nTips: $feedback"
    }
}