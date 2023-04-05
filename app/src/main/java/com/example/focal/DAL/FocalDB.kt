package com.example.focal

import android.annotation.SuppressLint
import android.util.Log
import com.example.focal.models.Attempt
import com.example.focal.models.Goal
import com.example.focal.models.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FocalDB{
    //Set the database variable to the root of the Firebase DB
    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("")

    //Get all the goals a user has of a specific exercise
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
    //Get all the goals a user has
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
    //Remove a goal from a user (used in 'complete goal')
    fun removeGoal(goal: Goal, callback: (String?) -> Unit){
        database.child("Goals").child(goal.userID.toString()).child(goal.exercise.toString()).child(goal.title.toString()).removeValue().addOnSuccessListener {
            callback("Goal has been successfully removed")
        }
    }
    //Add a user goal
    fun addGoal(goal: Goal, callback: (String?) -> Unit){
        database.child("Goals").child(goal.userID.toString()).child(goal.exercise.toString()).child(goal.title.toString()).setValue(goal).addOnSuccessListener {
            callback("Goal has been added successfully")
        }
    }
    //Replace the 'current' of the specified goal with the new updated value, then re-add to database
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
    //Get all users
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
    //Get a user by its user ID
    fun getUserByID(userID : String, callback:(User?) -> Unit){
        database.child("Users").child(userID).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            Log.e("User",user.toString())
            callback(user)
        }.addOnFailureListener {
            callback(null)
        }
    }
    //Add a new user to the database
    fun addUser(newUser: User) {
        database.child("Users").child(newUser.userID!!).setValue(newUser).addOnSuccessListener {
            Log.e("Firebase", "User has been registered")
        }
    }
    //Get the number of users in the database (Used to create an auto-incrementing ID for registration)
    fun getNumUsers(callback: (Int?) -> Unit) {
        getUsers { users->
            callback(users?.size)
        }
    }
    //Get all the attempts a user has made by their user ID
    @SuppressLint("NewApi")
    fun getAttempts(userID: String, callback: (MutableList<Attempt?>?) -> Unit) {
        database.child("Attempts").child(userID).get().addOnSuccessListener {
            val attempts = mutableListOf<Attempt?>()
            it.children.forEach {
                attempts.add(it.getValue(Attempt::class.java))
            }
            callback(attempts)
        }
    }
    //Add a new user attempt to the database
    fun addAttempt(userID: String, attempt: Attempt, timestamp: String,
                   callback: (String?) -> Unit) {
        database.child("Attempts").child(userID)
            .child(timestamp).setValue(attempt).addOnSuccessListener {
                callback("Attempt of timestamp $timestamp has been logged")
            }
    }

}