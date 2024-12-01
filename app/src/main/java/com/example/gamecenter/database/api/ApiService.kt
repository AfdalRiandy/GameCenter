package com.example.gamecenter.database.api

import com.example.gamecenter.database.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    fun getUserData(@Query("email") email: String): Call<List<User>>

    @FormUrlEncoded
    @POST("update_profile.php")
    fun updateProfile(
        @Field("email") email: String,
        @Field("full_name") fullName: String
    ): Call<UserResponse>

    @FormUrlEncoded
    @POST("add_room.php")
    fun addRoom(
        @Field("room_name") roomName: String,
        @Field("room_description") roomDescription: String,
        @Field("room_price") roomPrice: Double,
        @Field("image_url") imageUrl: String?
    ): Call<RoomResponse>

    @GET("get_rooms.php")
    fun getRooms(): Call<RoomResponse>

    @GET("get_foods.php")
    fun getFoods(): Call<FoodResponse>

    @Multipart
    @POST("upload_image.php")
    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadResponse>

    @GET("get_news.php")
    fun getNews(): Call<NewsResponse>

    @POST("add_news.php")
    fun addNews(@Body news: News): Call<News>



}
