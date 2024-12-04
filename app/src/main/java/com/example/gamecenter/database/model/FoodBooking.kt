package com.example.gamecenter.database.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FoodBooking(
    val foodId: Int,
    val foodName: String,
    val bookingDateTime: String,
    val paymentMethod: String,
    val totalPrice: Double
) : Parcelable