package com.example.gamecenter.model

data class BookingHistory(
    val id: Int,
    val roomName: String,
    val totalPrice: String,
    val roomDescription: String,
    val roomPrice: String,
    val imageUrl: String,
    val createAt: String,
    val duration: Int
)