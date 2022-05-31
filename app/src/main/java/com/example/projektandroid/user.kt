package com.example.projektandroid

data class User (
    //@SerializedName("body")
    val body: String,
    val _id: String,
    val username: String,
    val email: String,
    val password: String,
    val path: String
        )

//class users : ArrayList<usersItem>()