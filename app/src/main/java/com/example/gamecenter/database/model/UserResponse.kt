package com.example.gamecenter.database.model

data class UserResponse(
    val success: Boolean,
    val user_id: Int? = null,
    val email: String? = null,
    val message: String? = null,
    val role: String? = null
)
