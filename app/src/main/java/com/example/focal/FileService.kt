package com.example.focal

import android.app.Activity
import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

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
}