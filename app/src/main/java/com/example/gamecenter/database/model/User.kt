package com.example.gamecenter.database.model

data class User(
    val id: Int,
    val email: String,
    val full_name: String,
    val password: String,
    val role: String,
    val created_at: String
)
