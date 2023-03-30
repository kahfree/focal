package com.example.focal

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object FocalDB{
    private val database : DatabaseReference = FirebaseDatabase.getInstance().getReference("")

    fun readGoals(): List<Goal> {
        return mutableListOf()
    }

    fun updateGoalProgress(newCurrent: Float) {

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

    fun addAttempt(attempt: Attempt) {

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