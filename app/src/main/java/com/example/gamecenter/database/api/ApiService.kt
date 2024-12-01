package com.example.gamecenter.database.api

import com.example.gamecenter.database.model.*
import okhttp3.MultipartBody
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

    @GET("get_user_data.php")
    fun getUserData(): Call<List<User>>

    @GET("rooms")
    fun getRooms(): Call<List<Room>>

    @POST("rooms")
    fun addRoom(@Body room: Room): Call<Room>

    @GET("get_news.php")
    fun getNews(): Call<NewsResponse>

    @POST("add_news.php")
    fun addNews(@Body news: News): Call<News>

    @Multipart
    @POST("upload_image.php")
    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadResponse>

}
