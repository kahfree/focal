package com.example.focal

data class User(val userID : String? = null, val firstname: String? = null, val lastname : String? = null, val email : String? = null, val password : String? = null) {
    override fun toString(): String {
        return "User(userID=$userID, firstName=$firstname, lastname=$lastname, username=$email, password=$password)"
    }

}
