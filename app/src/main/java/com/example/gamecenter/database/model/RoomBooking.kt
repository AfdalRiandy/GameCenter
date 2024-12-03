package com.example.gamecenter.database.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomBooking(
    val roomId: Int,
    val roomName: String,
    val duration: Int,
    val bookingDateTime: String,
    val paymentMethod: String,
    val totalPrice: Double
) : Parcelable