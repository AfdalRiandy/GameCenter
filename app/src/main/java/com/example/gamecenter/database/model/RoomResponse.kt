package com.example.gamecenter.database.model

data class RoomResponse(
    val success: Boolean,
    val message: String,
    val data: List<Room>?
)
