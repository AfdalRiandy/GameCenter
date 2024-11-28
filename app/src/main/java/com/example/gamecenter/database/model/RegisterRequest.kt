package com.example.gamecenter.database.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)