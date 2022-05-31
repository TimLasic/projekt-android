package com.example.projektandroid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
//import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.example.projektandroid.databinding.FragmentCapturingBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

class CapturingFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentCapturingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sensorManager: SensorManager
    private var lastUpdateAccelerometer: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).getLocationUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCapturingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = "Capture"
        super.onViewCreated(view, savedInstanceState)
        lastUpdateAccelerometer = System.currentTimeMillis()

        /* val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
         val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager.unregisterListener(this)
        _binding = null
    }

    private fun setUpSensor() {
        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorAccelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            this,
            sensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_UI
        )

        val sensorGyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(
            this,
            sensorGyroscope,
            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_UI
        )

    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val alpha: Float = 0.8f
            var gy: Float = 0.0f
            var gx: Float = 0.0f
            var gz: Float = 0.0f

            var x: Float = 0.0f
            var y: Float = 0.0f
            var z: Float = 0.0f

            // Isolate the force of gravity with the low-pass filter.
            gx = alpha * gx + (1 - alpha) * event.values[0]
            gy = alpha * gy + (1 - alpha) * event.values[1]
            gz = alpha * gz + (1 - alpha) * event.values[2]

            // Remove the gravity contribution with the high-pass filter.
            x = event.values[0] - gx
            y = event.values[1] - gy
            z = event.values[2] - gz
            val time: Long = event.timestamp
            if (time - lastUpdateAccelerometer < 200) {
                return
            }
            lastUpdateAccelerometer = time


            Log.d("acce", "$x, $y, $z")

            binding.acceX.text = "X: " + x.toInt() + " m/s^2"
            binding.acceY.text = "Y: " + y.toInt() + " m/s^2"
            binding.acceZ.text = "Z: " + z.toInt() + " m/s^2"
        }

        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            var x: Float = 0.0f
            var y: Float = 0.0f
            var z: Float = 0.0f

            x = event.values[0]
            y = event.values[1]
            z = event.values[2]

            Log.d("gyro", "$x, $y, $z")

            binding.gyroX.text = "X: " + x.toInt() + " rad/s"
            binding.gyroY.text = "Y: " + y.toInt() + " rad/s"
            binding.gyroZ.text = "Z: " + z.toInt() + " rad/s"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //TODO("Not yet implemented")
        Log.d("zmaj", "erwtert");
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        (activity as MainActivity).stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).startLocationUpdates()
        setUpSensor()
    }

    private fun addAccelerometer(x: Float, y: Float, z: Float) {
        val accelerometer = Accelerometer(
            _id = "",
            body = "",
            x = x,
            y = y,
            z = z,
            locationId = ""
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

    private fun addGyroscope(x: Float, y: Float, z: Float) {
        val gyroscope = Gyroscope(
            _id = "",
            body = "",
            xRotation = x,
            yRotation = y,
            zRotation = z,
            locationId = ""
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

    private fun addLocation(latitude: Float, longitude: Float, state: String) {
        val location = Location(
            _id = "",
            body = "",
            latitude = latitude,
            longitude = longitude,
            state = state,
            timestamp = Date()
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
            }

            override fun onFailure(call: Call<Location?>, t: Throwable) {
                Log.d("CapturingFragment", "On failure, Accelerometer" + t.message)
            }
        })
    }
}
