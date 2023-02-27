package com.example.focal

data class User(val userID : Int? = null, val firstname: String? = null, val lastname : String? = null, val username : String? = null, val password : String? = null) {
    override fun toString(): String {
        return "User(userID=$userID, firstName=$firstname, lastname=$lastname, username=$username, password=$password)"
    }

}
