package com.example.projektandroid

import android.app.Application
import android.net.Uri
import android.os.FileUtils
import android.util.Log
import android.widget.Toast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*


class MyApplication : Application() {
    var roadId : String = ""
    var username : String = ""
    var locationId : String = ""
    var accelerometerX : Float = 0f
    var accelerometerY : Float = 0f
    var accelerometerZ : Float = 0f
    var gyroscopeX : Float = 0f
    var gyroscopeY : Float = 0f
    var gyroscopeZ : Float = 0f

    var accelerometerBaseX : Float = 100f
    var accelerometerBaseY : Float = 100f
    var accelerometerBaseZ : Float = 100f
    var gyroscopeBaseX : Float = 100f
    var gyroscopeBaseY : Float = 100f
    var gyroscopeBaseZ : Float = 100f

    var counterA : Int = 0
    var counterG : Int = 0
    var color : String = "Green"

    var userID: String = ""

    fun resetVariables () {
        accelerometerX = 0f
        accelerometerY = 0f
        accelerometerZ = 0f

        gyroscopeX = 0f
        gyroscopeY = 0f
        gyroscopeZ = 0f
    }
    fun resetVariablesBase () {
        accelerometerBaseX = 100f
        accelerometerBaseY = 100f
        accelerometerBaseZ = 100f

        gyroscopeBaseX = 100f
        gyroscopeBaseY = 100f
        gyroscopeBaseZ = 100f
    }

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

    fun algorithm() : String {
        var result : String = "green"
        var acceValue : Float = 0f
        var gyroValue : Float = 0f

        if (accelerometerBaseX == 100f && accelerometerBaseY == 100f && accelerometerBaseZ == 100f && gyroscopeBaseX == 100f && gyroscopeBaseY == 100f && gyroscopeBaseZ == 100f) {
            accelerometerBaseX = accelerometerX / counterA
            accelerometerBaseY = accelerometerY / counterA
            accelerometerBaseZ = accelerometerZ / counterA
            gyroscopeBaseX = gyroscopeX / counterG
            gyroscopeBaseY = gyroscopeY / counterG
            gyroscopeBaseZ = gyroscopeZ / counterG
            return result
        } else {
            if ((gyroscopeX / counterG) > gyroscopeBaseX) {
                gyroValue += (gyroscopeX / counterG) - gyroscopeBaseX
            }
            if ((gyroscopeY / counterG) > gyroscopeBaseY) {
                gyroValue += (gyroscopeY / counterG) - gyroscopeBaseY
            }
            if ((gyroscopeZ / counterG) > gyroscopeBaseZ) {
                gyroValue += (gyroscopeZ / counterG) - gyroscopeBaseZ
            }

            if (gyroValue < 3) {
                if ((accelerometerX / counterA) > accelerometerBaseX) {
                    acceValue += (accelerometerX / counterA) - accelerometerBaseX
                }
                if ((accelerometerY / counterA) > accelerometerBaseY) {
                    acceValue += (accelerometerY / counterA) - accelerometerBaseY
                }
                if ((accelerometerZ / counterA) > accelerometerBaseZ) {
                    acceValue += (accelerometerZ / counterA) - accelerometerBaseZ
                }

                if (acceValue <= 3) {
                    result = "green"
                } else if (acceValue in 4.0..8.0) {
                    result = "orange"
                } else if (acceValue >= 9) {
                    result = "red"
                }
            } else {
                result = "black"
            }
        }

        return result
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

                    addAccelerometer(accelerometerX/counterA, accelerometerY/counterA, accelerometerZ/counterA, locationId)
                    addGyroscope(gyroscopeX/counterG, gyroscopeY/counterG, gyroscopeZ/counterG, locationId)

                    resetVariables()

                    counterA = 0
                    counterG = 0
                }
            }

            override fun onFailure(call: Call<Location?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Accelerometer" + t.message)
            }
        })
    }

    fun upload(originalFile : File) {

        var userId = RequestBody.create(MultipartBody.FORM, userID)

        var fileUri = Uri.fromFile(originalFile)

        var filePart = RequestBody.create(
            MediaType.parse(contentResolver.getType(fileUri).toString()),
            originalFile
        )

        var file = MultipartBody.Part.createFormData("image", originalFile.name, filePart)

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")

        var retrofit = retrofitBuilder.build()

        var client = retrofit.create(ApiInterface::class.java)

        var call = client.upload(file, userId)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                Toast.makeText(applicationContext, "yeah!", Toast.LENGTH_SHORT).show()
                Log.d("YEAH!", "fileCreated")
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Image: " + t.message)
            }
        })
    }

}

/*fun upload(file:File) {

        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://projekt-glz.herokuapp.com/")
            .build()
            .create(ApiInterface::class.java)


        val requestFile : RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val multipart : MultipartBody.Part = MultipartBody.Part.createFormData("image",file.name,requestFile)
        val requestBody : RequestBody = RequestBody.create(MediaType.parse("text/plain"),"image")
        Log.d("multipart ", multipart.toString())
        Log.d("requestBody ", requestBody.toString())
        val retrofitData = retrofitBuilder.upload(multipart, requestBody)

        retrofitData.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                //Snackbar.make(view, response.message(), Snackbar.LENGTH_SHORT).show() //Successfully logged in

                if (response.code() == 200) {
                    Log.d("ZMAJ","Uploaded Successfully!");
                }

                if (response.message() == "OK") {

                    Log.d("CapturingFragment", "On success, Image: " + response.message())
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Image: " + t.message)
            }
        })
    }*/