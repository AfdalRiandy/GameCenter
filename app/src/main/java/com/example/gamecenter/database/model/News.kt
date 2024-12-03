package com.example.gamecenter.database.model

data class News(
    val id: Int,
    val title: String,
    val content: String,
    val image_url: String,
    val timestamp: String
)