package com.example.gamecenter.database.model

data class UploadResponse(
    val success: Boolean,
    val message: String? = null,
    val image_url: String? = null
)
