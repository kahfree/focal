package com.example.focal

import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.create()
    var firstname: String = ""
    var lastname: String = ""
    var email : String = ""
    var password : String = ""
}