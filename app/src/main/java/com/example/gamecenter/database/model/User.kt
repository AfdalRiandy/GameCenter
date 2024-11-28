package com.example.gamecenter.database.model

data class User(
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val phone_number: String
)