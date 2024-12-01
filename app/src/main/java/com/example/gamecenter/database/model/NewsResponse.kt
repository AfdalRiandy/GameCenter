package com.example.gamecenter.database.model

data class NewsResponse(
    val success: Boolean,
    val message: String? = null,
    val status: String,
    val news: List<News>,
    val data: List<News>? = null
)