package com.example.gamecenter.database.repository

import com.example.gamecenter.database.api.RetrofitClient
import com.example.gamecenter.database.model.LoginRequest
import com.example.gamecenter.database.model.RegisterRequest

class UserRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun login(email: String, password: String) =
        apiService.login(LoginRequest(email, password))

    suspend fun register(fullName: String, email: String, password: String) =
        apiService.register(RegisterRequest(fullName, email, password))
}