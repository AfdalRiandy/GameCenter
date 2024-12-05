package com.example.gamecenter.database.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Food(
    val id: Int,
    val food_name: String,
    val food_price: String,
    val food_description: String,
    val image_url: String
)   :Parcelable

