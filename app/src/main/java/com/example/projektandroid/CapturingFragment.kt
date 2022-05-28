package com.example.projektandroid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
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

class CapturingFragment : Fragment(), SensorEventListener, LocationListener {
    private var _binding: FragmentCapturingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sensorManager: SensorManager
    //private lateinit var locationManager: LocationManager
    private var lastUpdateAccelerometer: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    }

    /*private fun setUpLocation() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }


        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        val location : Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


    }*/

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


        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //TODO("Not yet implemented")
        Log.d("zmaj", "erwtert");
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()

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

    override fun onLocationChanged(location: Location) {

    }


}