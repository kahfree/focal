package com.example.focal

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object FocalDB{
    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("")

    fun getGoalsByExercise(userID :String, exercise: String, callback: (MutableList<Goal?>?) -> Unit){

        database.child("Goals").child(userID).child(exercise).get().addOnSuccessListener {
            val goals = mutableListOf<Goal?>()
            it.children.forEach {
                goals.add(it.getValue(Goal::class.java))
                callback(goals)
            }
            goals.forEach {
                Log.e("Get All Goals", it.toString())
            }
        }
    }

    fun getGoalsByUserID(userID :String, callback: (MutableList<Goal?>?) -> Unit){
        val goals = mutableListOf<Goal?>()
        database.child("Goals").child(userID).get().addOnSuccessListener {
            it.children.forEach {
                it.children.forEach {
                    goals.add(it.getValue(Goal::class.java))
                }
            }
            goals.forEach {
                Log.e("Get All Goals", it.toString())
            }
            callback(goals)
        }
    }

    fun removeGoal(goal: Goal, callback: (String?) -> Unit){
        database.child("Goals").child(goal.userID.toString()).child(goal.exercise.toString()).child(goal.title.toString()).removeValue().addOnSuccessListener {
            callback("Goal has been successfully removed")
        }
    }

    fun addGoal(goal: Goal, callback: (String?) -> Unit){
        database.child("Goals").child(goal.userID.toString()).child(goal.exercise.toString()).child(goal.title.toString()).setValue(goal).addOnSuccessListener {
            callback("Goal has been added successfully")
        }
    }

    fun updateGoalProgress(userID: String, newCurrent: Float, goal : Goal, callback: (String?) -> Unit) {
        val oldCurrent = goal.current
        val updatedGoal: Goal = Goal(
            goal.goalID,
            goal.userID,
            goal.exercise,
            goal.goal,
            newCurrent,
            goal.deadline,
            goal.title,
            goal.status
        )
        Log.e("Goal to Update", updatedGoal.toString())
        database.child("Goals")
            .child(userID)
            .child(goal.exercise!!)
            .child(goal.title!!)
            .setValue(updatedGoal).addOnSuccessListener {
            callback("Updated ${goal.title} goal from $oldCurrent to $newCurrent")
        }
    }
    fun updateGoalStatus(newStatus: String) {

    }

    fun logGoals() {

    }

    fun resetGoals(){

    }

    fun getUsers(callback: (MutableList<User?>?) -> Unit){

        database.child("Users").get().addOnSuccessListener {
            val users = mutableListOf<User?>()
            it.children.forEach {
                users.add(it.getValue(User::class.java))
                callback(users)
            }
            users.forEach {
                Log.e("UserList", it.toString())
            }
        }
    }

    fun getUserByID(userID : String, callback:(User?) -> Unit){
        database.child("Users").child(userID).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            Log.e("User",user.toString())
            callback(user)
        }.addOnFailureListener {
            callback(null)
        }
    }
    fun addUser(newUser: User) {
        database.child("Users").child(newUser.userID!!).setValue(newUser).addOnSuccessListener {
            Log.e("Firebase", "User has been registered")
        }
    }

    fun logUsers() {

    }

    fun resetUsers(){

    }

    fun getNumUsers(callback: (Int?) -> Unit) {
        getUsers(){users->
            callback(users?.size)
        }
    }

    @SuppressLint("NewApi")
    fun getAttempts(userID: String, callback: (MutableList<Attempt?>?) -> Unit) {
        database.child("Attempts").child(userID).get().addOnSuccessListener {
            var attempts = mutableListOf<Attempt?>()
            it.children.forEach {
                attempts.add(it.getValue(Attempt::class.java))
            }
            callback(attempts)
        }
    }

    fun getAttemptsByUserID(userID: Int): List<Attempt> {
        return mutableListOf()
    }

    @SuppressLint("NewApi")
    fun getAttemptsByUserIDAndExercise(userID: Int,exercise: String): List<Attempt> {
        return mutableListOf()
    }

    fun logAttempts() {

    }

    fun resetAttempts(){

    }

    fun addAttempt(userID: String, attempt: Attempt, timestamp: String,
        callback: (String?) -> Unit) {
        database.child("Attempts").child(userID)
            .child(timestamp).setValue(attempt).addOnSuccessListener {
                callback("Attempt of timestamp $timestamp has been logged")
            }
    }

    fun getNumRowsAttempts(): Int {
        return 0
    }

    @SuppressLint("NewApi")
    fun getGoals(): List<Goal> {
        return mutableListOf()
    }

    fun getGoalsByUserID(userID : String) : List<Goal> {
        return mutableListOf()
    }
}