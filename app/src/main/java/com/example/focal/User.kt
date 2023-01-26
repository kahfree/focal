package com.example.focal

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User(
    userID : String,
    firstname : String,
    lastname : String,
    email: String,
    password: String
) {
private var attemptHistory : List<Attempt>? = null
}