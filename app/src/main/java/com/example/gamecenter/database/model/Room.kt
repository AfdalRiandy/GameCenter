package com.example.gamecenter.database.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    val id: Int,
    val room_name: String,
    val room_price: String,
    val room_description: String,
    val image_url: String
) : Parcelable


