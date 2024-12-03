package com.example.gamecenter.database.model

data class RoomBookingResponse(
    val success: Boolean,
    val message: String,
    val bookingId: Int? = null
)