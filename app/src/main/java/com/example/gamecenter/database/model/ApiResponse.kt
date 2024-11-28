package com.example.gamecenter.database.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)