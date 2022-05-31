package com.example.projektandroid

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

}