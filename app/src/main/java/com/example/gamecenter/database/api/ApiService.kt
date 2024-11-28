package com.example.gamecenter.database.api

import com.example.gamecenter.database.model.ApiResponse
import com.example.gamecenter.database.model.LoginRequest
import com.example.gamecenter.database.model.RegisterRequest
import com.example.gamecenter.database.model.User
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): ApiResponse<User>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): ApiResponse<User>
}