package com.example.gamecenter.database

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userType: String
)