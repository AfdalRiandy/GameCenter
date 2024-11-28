package com.example.gamecenter.database.repository

import com.example.gamecenter.database.api.RetrofitClient
import com.example.gamecenter.database.model.LoginRequest
import com.example.gamecenter.database.model.RegisterRequest
import com.example.gamecenter.util.PreferenceManager
import android.content.Context

class AuthRepository(private val context: Context) {
    private val ApiService = RetrofitClient.apiService
    private val prefManager = PreferenceManager(context)

    suspend fun login(email: String, password: String) =
        ApiService.login(LoginRequest(email, password))

    suspend fun register(fullName: String, email: String, password: String) =
        ApiService.register(RegisterRequest(fullName, email, password))
}