package com.example.focal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileService(
    val activity: Activity
) {
    fun readGoals(): List<Goal> {
        var goalList = mutableListOf<Goal>()
        val goalsFileInput = activity.openFileInput("goals.txt")
        val text = goalsFileInput.bufferedReader().use { it.readLines() }
        for (line in text) {
            var tmpList = line.split(",")
            goalList.add(
                Goal(
                    tmpList[0].toInt(),
                    tmpList[1].toInt(),
                    tmpList[2],
                    tmpList[3].toFloat(),
                    tmpList[4].toFloat(),
                    tmpList[5],
                    tmpList[6],
                    tmpList[7]
                )
            )
        }
        return goalList
    }

    fun updateGoalProgress(newCurrent: Float) {
        var goal = readGoals()
        val fileOutputStream = activity.openFileOutput("goals.txt", Context.MODE_PRIVATE)
        val printWriter = PrintWriter(fileOutputStream)
        goal[0].current = newCurrent
        Log.e("File Service Update", goal[0].toString())
        printWriter.println(goal[0].toString())
        printWriter.flush()
        printWriter.close()
        fileOutputStream.close()
    }
    fun updateGoalStatus(newStatus: String) {
        var goal = readGoals()
        val fileOutputStream = activity.openFileOutput("goals.txt", Context.MODE_PRIVATE)
        val printWriter = PrintWriter(fileOutputStream)
        goal[0].status = newStatus
        Log.e("File Service Update", goal[0].toString())
        printWriter.println(goal[0].toString())
        printWriter.flush()
        printWriter.close()
        fileOutputStream.close()
    }

    fun logGoals() {
        val goalsFileInput = activity.openFileInput("goals.txt")
        val goalsText = goalsFileInput.bufferedReader().use { it.readText() }
        Log.e("File Service Goals",goalsText)
    }

    fun resetGoals(){
        val fileOutputStream = activity.openFileOutput("goals.txt", Context.MODE_PRIVATE)
        val printWriter = PrintWriter(fileOutputStream)
        printWriter.println("1,1,Squat,45,54.76,13-02-2023,Max Depth,Ongoing")
        printWriter.println("2,1,Squat,85,73.22,08-02-2023,Quality,Ongoing")
        printWriter.flush()
        printWriter.close()
        fileOutputStream.close()
    }

    fun getUsers(): List<User> {
        var users = mutableListOf<User>()
        val userFileInput = activity.openFileInput("users.txt")
        val text = userFileInput.bufferedReader().use { it.readLines() }
        for (line in text) {
            var tmpList = line.split(",")
            users.add(
                User(
                    tmpList[0].toInt(),
                    tmpList[1],
                    tmpList[2],
                    tmpList[3],
                    tmpList[4],
                )
            )
        }
        return users
    }

    fun addUser(user: User) {
        val usersFileOutput = activity.openFileOutput("users.txt", Context.MODE_APPEND)
        val newData = "$user\n"
        usersFileOutput.write(newData.toByteArray())
        usersFileOutput.close()
    }

    fun logUsers() {
        val usersFileInput = activity.openFileInput("users.txt")
        val usersText = usersFileInput.bufferedReader().use { it.readText() }
        Log.e("File Service Users",usersText)
    }

//    1,John,Doe,johndoe@gmail.com,johndoe24
//    2,Jane,Doe,janedoe@gmail.com,janedoe32
//    3,Marty,Morrissey,mm58@gmail.com,martyparty
    fun resetUsers(){
        val fileOutputStream = activity.openFileOutput("users.txt", Context.MODE_PRIVATE)
        val printWriter = PrintWriter(fileOutputStream)
        printWriter.println("1,John,Doe,johndoe@gmail.com,johndoe24")
        printWriter.println("2,Jane,Doe,janedoe@gmail.com,janedoe32")
        printWriter.println("3,Marty,Morrissey,mm58@gmail.com,martyparty")
        printWriter.flush()
        printWriter.close()
        fileOutputStream.close()
    }

    fun getNumRowsUsers(): Int {
        return getUsers().size
    }

    @SuppressLint("NewApi")
    fun getAttempts(): List<Attempt> {
        var attempts = mutableListOf<Attempt>()
        val userFileInput = activity.openFileInput("attempts.txt")
        val text = userFileInput.bufferedReader().use { it.readLines() }
        for (line in text) {
            var tmpList = line.split(",")
            attempts.add(
                Attempt(
                    tmpList[0].toInt(),
                    tmpList[1].toInt(),
                    tmpList[2],
                    tmpList[3],
                    tmpList[4].toFloat(),
                    tmpList[5].toFloat(),
                    tmpList[6]
                )
            )
        }
        return attempts
    }
    @SuppressLint("NewApi")
    fun getAttemptsByUserID(userID: Int): List<Attempt> {
        var attempts = mutableListOf<Attempt>()
        val userFileInput = activity.openFileInput("attempts.txt")
        val text = userFileInput.bufferedReader().use { it.readLines() }
        for (line in text) {
            var tmpList = line.split(",")
            if(tmpList[1].toInt() == userID) {
                attempts.add(
                    Attempt(
                        tmpList[0].toInt(),
                        tmpList[1].toInt(),
                        tmpList[2],
                        tmpList[3],
                        tmpList[4].toFloat(),
                        tmpList[5].toFloat(),
                        tmpList[6]
                    )
                )
            }
        }
        return attempts
    }

    @SuppressLint("NewApi")
    fun getAttemptsByUserIDAndExercise(userID: Int,exercise: String): List<Attempt> {
        var attempts = mutableListOf<Attempt>()
        val userFileInput = activity.openFileInput("attempts.txt")
        val text = userFileInput.bufferedReader().use { it.readLines() }
        for (line in text) {
            var tmpList = line.split(",")
            if(tmpList[1].toInt() == userID && tmpList[2] == exercise) {
                attempts.add(
                    Attempt(
                        tmpList[0].toInt(),
                        tmpList[1].toInt(),
                        tmpList[2],
                        tmpList[3],
                        tmpList[4].toFloat(),
                        tmpList[5].toFloat(),
                        tmpList[6]
                    )
                )
            }
        }
        return attempts
    }

    fun logAttempts() {
        val attemptsFileInput = activity.openFileInput("attempts.txt")
        val usersText = attemptsFileInput.bufferedReader().use { it.readText() }
        Log.e("File Service Attempts",usersText)

    }

    fun resetAttempts(){
        val fileOutputStream = activity.openFileOutput("attempts.txt", Context.MODE_PRIVATE)
        val printWriter = PrintWriter(fileOutputStream)
//        2023-02-02T10:33:11.422941
//        02-02-2023 10:33:11
//        28-01-2023 12:30:00
//        2023-01-28T12:30:00.43
        printWriter.println("1,1,Squat,28-01-2023 12:30:00,40.34,73.56,Knees Out!-Wider Stance!")
        printWriter.println("2,1,Squat,28-01-2023 12:37:00,36.26,77.32,Wider Stance!")
        printWriter.println("3,1,Shoulder Press,02-02-2023 10:33:11,60.69,92.6,Both Arms!")
        printWriter.println("4,1,Shoulder Press,02-02-2023 10:56:46,50.55,93.38,Both Arms!")
        printWriter.flush()
        printWriter.close()
        fileOutputStream.close()
    }

    fun addAttempt(attempt: Attempt) {
        val attemptsFieOutput = activity.openFileOutput("attempts.txt", Context.MODE_APPEND)
        val newData = "$attempt\n"
        attemptsFieOutput.write(newData.toByteArray())
        attemptsFieOutput.close()
        Log.e("File Service","Added new attempt: $attempt")
        logAttempts()
    }

    fun getNumRowsAttempts(): Int {
        return getAttempts().size
    }

    fun getUserByID(userID: Int): User{
        val users = getUsers()
        for(user in users){
            if(user.userID == userID)
                return user
        }
        return User(0,"n/a","n/a","n/a","n/a")
    }
}