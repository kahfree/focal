package com.example.focal

class UserTest(
    userID : Int,
    firstname : String,
    lastname : String,
    email: String,
    password: String
) {
    val userID = userID
    val firstname = firstname
    val lastname = lastname
    val email = email
    var password = password
    override fun toString(): String {
        return "$userID,$firstname,$lastname,$email,$password"
    }

}