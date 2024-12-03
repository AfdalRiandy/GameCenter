package com.example.gamecenter.database.model

data class FoodBookingResponse(
    val success: Boolean,
    val message: String,
    val bookingId: Int? = null
)