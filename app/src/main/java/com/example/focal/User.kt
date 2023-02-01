package com.example.focal

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User(
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