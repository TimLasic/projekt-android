package com.example.projektandroid

import android.media.Image
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @GET("users")
    fun getUsers(): Call<List<User>>

    @POST("users/login")
    fun logInUser(@Body Userdata: User): Call<User>

    @POST("users")
    fun addUser(@Body Userdata: User):Call<User>

    @POST("locations")
    fun addLocation(@Body LocationData: Location):Call<Location>

    @POST("accelerometers")
    fun addAccelerometer(@Body AccelerometerData: Accelerometer):Call<Accelerometer>

    @POST("gyroscopes")
    fun addGyroscope(@Body GyroscopeData: Gyroscope):Call<Gyroscope>

    @POST("roads")
    fun addRoad(@Body RoadData: Road):Call<Road>

    @Multipart
    @POST("users/upload")
    fun upload(
        @Part image:MultipartBody.Part,
        @Part("id") id: RequestBody
               ):Call<Any>

}