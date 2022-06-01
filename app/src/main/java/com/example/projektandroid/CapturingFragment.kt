package com.example.projektandroid

import android.annotation.SuppressLint
import android.content.Context

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
//import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.projektandroid.databinding.FragmentCapturingBinding
import java.lang.Math.abs

class CapturingFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentCapturingBinding? = null
    private val binding get() = _binding!!
    private lateinit var sensorManager: SensorManager
    private var lastUpdateAccelerometer: Long = 0
    private lateinit var app : MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).getLocationUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        app = activity?.application as MyApplication
        _binding = FragmentCapturingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = "Capture"
        super.onViewCreated(view, savedInstanceState)
        lastUpdateAccelerometer = System.currentTimeMillis()

        binding.btnStart.setOnClickListener {
            app.addRoad("road")

            (activity as MainActivity).startLocationUpdates()
            setUpSensor()
        }

        binding.btnStop.setOnClickListener {
            sensorManager.unregisterListener(this)
            (activity as MainActivity).stopLocationUpdates()
            //app.roadId = ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sensorManager.unregisterListener(this)
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
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

            app.counterA += 1

            app.accelerometer.x = (app.accelerometer.x + kotlin.math.abs(x))
            app.accelerometer.y = (app.accelerometer.y + kotlin.math.abs(y)) / app.counterA
            app.accelerometer.z = (app.accelerometer.z + kotlin.math.abs(z)) / app.counterA
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

            app.counterG += 1

            app.gyroscope.xRotation = (app.gyroscope.xRotation + kotlin.math.abs(x)) / app.counterG
            app.gyroscope.yRotation = (app.gyroscope.yRotation + kotlin.math.abs(y)) / app.counterG
            app.gyroscope.zRotation = (app.gyroscope.zRotation + kotlin.math.abs(z)) / app.counterG
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //TODO("Not yet implemented")
        Log.d("zmaj", "erwtert");
    }

}
