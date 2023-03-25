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

    fun getUsers(): MutableList<User?> {
        var users = mutableListOf<User?>()
        database.child("Users").get().addOnSuccessListener {
            it.children.forEach {
                users.add(it.getValue(User::class.java))
            }
            users.forEach {
                Log.e("UserList", it.toString())
            }
        }
        return users
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

    fun getNumUsers(): Int {
        return getUsers().size
    }

    @SuppressLint("NewApi")
    fun getAttempts(): List<Attempt> {
        return mutableListOf()
    }
    @SuppressLint("NewApi")
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