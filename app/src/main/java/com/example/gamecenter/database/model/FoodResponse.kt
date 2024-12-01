package com.example.gamecenter.database.model

data class FoodResponse(
    val success: Boolean,
    val message: String,
    val data: List<Food>?
)
