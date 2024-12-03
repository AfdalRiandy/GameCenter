package com.example.gamecenter.database.model

data class Transaction(
    val id: Int,
    val userName: String,
    val itemName: String,
    val description: String,
    val totalPrice: String,
    val publishDate: String,
    val type: TransactionType
)

enum class TransactionType {
    ROOM, FOOD
}