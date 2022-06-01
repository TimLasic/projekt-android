package com.example.projektandroid

import android.app.Application
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MyApplication : Application() {
    var roadId : String = ""
    var locationId : String = ""
    var location : Location = Location("", "", 0f, 0f, "", Date(), "")
    var accelerometer: Accelerometer = Accelerometer("", "", 0f, 0f, 0f, "")
    var gyroscope: Gyroscope = Gyroscope("", "", 0f, 0f, 0f, "")
    var counterA : Int = 0
    var counterG : Int = 0

    override fun onCreate() {
        super.onCreate()
    }

    fun addAccelerometer(x: Float, y: Float, z: Float, locationId: String) {
        val accelerometer = Accelerometer(
            _id = "",
            body = "",
            x = x,
            y = y,
            z = z,
            locationId = locationId
        )

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")
            .build()
            .create(ApiInterface::class.java)


        val retrofitData = retrofitBuilder.addAccelerometer(accelerometer)
        retrofitData.enqueue(object : Callback<Accelerometer?> {
            override fun onResponse(
                call: Call<Accelerometer?>,
                response: Response<Accelerometer?>
            ) {
                //Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show() //Successfully logged in
                if (response.message() == "OK") {

                    Log.d("CapturingFragment", "On failure, Accelerometer" + response.message())
                }
            }

            override fun onFailure(call: Call<Accelerometer?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Accelerometer" + t.message)
            }
        })
    }

    fun addGyroscope(x: Float, y: Float, z: Float, locationId: String) {
        val gyroscope = Gyroscope(
            _id = "",
            body = "",
            xRotation = x,
            yRotation = y,
            zRotation = z,
            locationId = locationId
        )

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")
            .build()
            .create(ApiInterface::class.java)


        val retrofitData = retrofitBuilder.addGyroscope(gyroscope)
        retrofitData.enqueue(object : Callback<Gyroscope?> {
            override fun onResponse(
                call: Call<Gyroscope?>,
                response: Response<Gyroscope?>
            ) {
                //Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show() //Successfully logged in
                if (response.message() == "OK") {

                    Log.d("CapturingFragment", "On failure, Accelerometer" + response.message())
                }
            }

            override fun onFailure(call: Call<Gyroscope?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Gyroscope" + t.message)
            }
        })
    }



    fun addRoad(name: String) {
        val road = Road(
            _id = "",
            body = "",
            name = name
        )

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")
            .build()
            .create(ApiInterface::class.java)


        val retrofitData = retrofitBuilder.addRoad(road)
        retrofitData.enqueue(object : Callback<Road?> {
            override fun onResponse(
                call: Call<Road?>,
                response: Response<Road?>
            ) {
                //Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show() //Successfully logged in
                if (response.message() == "OK") {
                    Log.d("CapturingFragment", "On success, Road" + roadId)
                }
                if (response.message() == "Created") {
                    roadId = response.body()!!._id

                    if (location.roadId == "") {
                        addLocation(location.latitude, location.longitude, location.state)
                        location.roadId = roadId
                    }
                }
            }

            override fun onFailure(call: Call<Road?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Accelerometer" + t.message)
            }
        })
    }

    fun addLocation(latitude: Float, longitude: Float, state: String) {
        val location = Location(
            _id = "",
            body = "",
            latitude = latitude,
            longitude = longitude,
            state = state,
            timestamp = Date(),
            roadId = roadId
        )

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")
            .build()
            .create(ApiInterface::class.java)


        val retrofitData = retrofitBuilder.addLocation(location)
        retrofitData.enqueue(object : Callback<Location?> {
            override fun onResponse(
                call: Call<Location?>,
                response: Response<Location?>
            ) {
                //Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show() //Successfully logged in
                if (response.message() == "OK") {

                    Log.d("CapturingFragment", "On failure, Accelerometer" + response.message())
                }

                if (response.message() == "Created") {
                    locationId = response.body()!!._id

                    addAccelerometer(accelerometer.x, accelerometer.y, accelerometer.z, locationId)
                    addGyroscope(gyroscope.xRotation, gyroscope.yRotation, gyroscope.zRotation, locationId)

                    gyroscope = Gyroscope("", "", 0f, 0f, 0f, response.body()!!._id)
                    accelerometer = Accelerometer("", "", 0f, 0f, 0f, response.body()!!._id)
                    counterA = 0
                    counterG = 0
                }
            }

            override fun onFailure(call: Call<Location?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Accelerometer" + t.message)
            }
        })
    }

}