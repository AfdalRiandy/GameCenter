package com.example.gamecenter.database.model

data class News(
    val id: Int,
    val title: String,
    val content: String,
    val imageUrl: String,
    val timestamp: String
)