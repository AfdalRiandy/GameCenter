package com.example.gamecenter.database.api

import com.example.gamecenter.database.model.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("register.php")
    fun register(
        @Field("email") email: String,
        @Field("full_name") fullName: String,
        @Field("password") password: String
    ): Call<UserResponse>

}
