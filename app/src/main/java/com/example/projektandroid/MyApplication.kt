package com.example.projektandroid

import android.app.Application
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class MyApplication : Application() {
    var roadId : String = ""
    var locationId : String = ""
    var accelerometer: Accelerometer = Accelerometer("", "", 0f, 0f, 0f, "")
    var gyroscope: Gyroscope = Gyroscope("", "", 0f, 0f, 0f, "")
    var counterA : Int = 0
    var counterG : Int = 0

    /*
    //TODO
    // arrays for our data
    var accelerometerListX = ArrayList<Int>()
    var accelerometerListY = ArrayList<Int>()
    var accelerometerListZ = ArrayList<Int>()

    var gyroscopeListX = ArrayList<Int>()
    var gyroscopeListY = ArrayList<Int>()
    var gyroscopeListZ = ArrayList<Int>()

    //TODO
    // tmp arrays
    var accelerometerListXtmp = ArrayList<Int>()
    var accelerometerListYtmp = ArrayList<Int>()
    var accelerometerListZtmp = ArrayList<Int>()

    var gyroscopeListXtmp = ArrayList<Int>()
    var gyroscopeListYtmp = ArrayList<Int>()
    var gyroscopeListZtmp = ArrayList<Int>()
    */

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
                }
            }

            override fun onFailure(call: Call<Road?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Accelerometer" + t.message)
            }
        })
    }

    /*fun algoritem () {
        //TODO
        // dobimo max vrednosti vseh arrayov in podarrayi se nastavijo vse na 0
        var i = 0
        val accelerometerListXmax = accelerometerListX.maxOrNull() ?: 0
        while (i < accelerometerListXmax+1) {
            accelerometerListXtmp.add(0)
            i+=1
        }

        val accelerometerListYmax = accelerometerListY.maxOrNull() ?: 0
        i=0
        while (i < accelerometerListYmax+1) {
            accelerometerListYtmp.add(0)
            i+=1
        }

        val accelerometerListZmax = accelerometerListZ.maxOrNull() ?: 0
        i=0
        while (i < accelerometerListZmax+1) {
            accelerometerListZtmp.add(0)
            i+=1
        }

        val gyroscopeListXmax = gyroscopeListX.maxOrNull() ?: 0
        i=0
        while (i < gyroscopeListXmax+1) {
            gyroscopeListXtmp.add(0)
            i+=1
        }

        val gyroscopeListYmax = gyroscopeListY.maxOrNull() ?: 0
        i=0
        while (i < gyroscopeListYmax+1) {
            gyroscopeListYtmp.add(0)
            i+=1
        }

        val gyroscopeListZmax = gyroscopeListZ.maxOrNull() ?: 0
        i=0
        while (i < gyroscopeListZmax+1) {
            gyroscopeListZtmp.add(0)
            i+=1
        }

        //TODO
        // count number of items in array to tmp arrays
        i=0
        while (i < accelerometerListX.size) {
            accelerometerListXtmp[accelerometerListX[i]] += 1
            i+=1
        }
        i=0
        while (i < accelerometerListY.size) {
            accelerometerListYtmp[accelerometerListY[i]] += 1
            i+=1
        }
        i=0
        while (i < accelerometerListZ.size) {
            accelerometerListZtmp[accelerometerListZ[i]] += 1
            i+=1
        }

        i=0
        while (i < gyroscopeListX.size) {
            gyroscopeListXtmp[gyroscopeListX[i]] += 1
            i+=1
        }
        i=0
        while (i < gyroscopeListY.size) {
            gyroscopeListYtmp[gyroscopeListY[i]] += 1
            i+=1
        }
        i=0
        while (i < gyroscopeListZ.size) {
            gyroscopeListZtmp[gyroscopeListZ[i]] += 1
            i+=1
        }
    }*/


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

                    addAccelerometer(accelerometer.x/counterA, accelerometer.y/counterA, accelerometer.z/counterA, locationId)
                    addGyroscope(gyroscope.xRotation/counterG, gyroscope.yRotation/counterG, gyroscope.zRotation/counterG, locationId)

                    gyroscope = Gyroscope("", "", 0f, 0f, 0f, response.body()!!._id)
                    accelerometer = Accelerometer("", "", 0f, 0f, 0f, response.body()!!._id)
                    counterA = 0
                    counterG = 0
                    /*accelerometerListX.clear()
                    accelerometerListY.clear()
                    accelerometerListZ.clear()

                    gyroscopeListX.clear()
                    gyroscopeListY.clear()
                    gyroscopeListZ.clear()

                    accelerometerListXtmp.clear()
                    accelerometerListYtmp.clear()
                    accelerometerListZtmp.clear()

                    gyroscopeListXtmp.clear()
                    gyroscopeListYtmp.clear()
                    gyroscopeListZtmp.clear()*/
                }
            }

            override fun onFailure(call: Call<Location?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Accelerometer" + t.message)
            }
        })
    }

}