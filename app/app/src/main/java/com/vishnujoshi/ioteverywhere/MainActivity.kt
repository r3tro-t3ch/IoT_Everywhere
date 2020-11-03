package com.vishnujoshi.ioteverywhere

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View

import com.vishnujoshi.ioteverywhere.compiler.compiler
import com.vishnujoshi.ioteverywhere.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private final val TAG = "MainActivity"

    var SENSOR_DATA : SharedPreferences ?= null
    private lateinit var sensorManager : SensorManager
    private var TEMPERATURE : Sensor? = null
    private var LIGHT : Sensor ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        TEMPERATURE = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        LIGHT = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        //shared preference to provide data to evaluator class
        SENSOR_DATA = getSharedPreferences("SENSOR_DATA", Context.MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.run.setOnClickListener{

            val code = binding.codeEditor.text.trim().toString()

            val c = compiler(code + "\u0000")
            c.context = applicationContext
            c.compile()

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(outputScreen.visibility == View.VISIBLE){
            outputScreen.visibility = View.GONE
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val editor = SENSOR_DATA!!.edit()

        val temperature = event!!.values[0]
        val light = event!!.values[1]

        editor.putString("TEMPERATURE", temperature.toString())
        editor.putString("LIGHT", light.toString());

        editor.commit()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, TEMPERATURE, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

}