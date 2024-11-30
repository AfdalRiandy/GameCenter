package com.example.gamecenter.database.model

data class UserResponse(
    val success: Boolean,
    val email: String? = null,
    val message: String? = null,
    val role: String?
)