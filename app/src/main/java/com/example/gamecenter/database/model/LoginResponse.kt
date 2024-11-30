package com.example.gamecenter.database.model

data class LoginResponse(
    val success: Boolean,
    val email: String? = null,
    val message: String? = null
)