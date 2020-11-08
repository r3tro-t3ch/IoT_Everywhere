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

    //sensors
    private var TEMPERATURE : Sensor? = null
    private var LIGHT : Sensor ? = null
    private var GYROSCOPE : Sensor ? = null
    private var ACCELEROMETER : Sensor ? = null
    private var HUMIDITY : Sensor ? = null
    private var AIR_PRESSURE : Sensor ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //setting Activity binder
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //initializing shared preferences
        //to provide data to evaluator class
        SENSOR_DATA = getSharedPreferences("SENSOR_DATA", Context.MODE_PRIVATE)

        //initializing sensor modules
        initSensor()

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

        val s = event!!.sensor

        if(s.type == Sensor.TYPE_PROXIMITY){

            editor.putString("LIGHT", event!!.values[0].toString())

        }else if(s.type == Sensor.TYPE_AMBIENT_TEMPERATURE){

            editor.putString("TEMPERATURE", event!!.values[0].toString())

        }else if(s.type == Sensor.TYPE_GYROSCOPE){

            editor.putString("GYROSCOPEX", event!!.values[0].toString())
            editor.putString("GYROSCOPEY", event!!.values[1].toString())
            editor.putString("GYROSCOPEZ", event!!.values[2].toString())

        }else if(s.type == Sensor.TYPE_ACCELEROMETER){

            editor.putString("ACCELEROMETERX", event!!.values[0].toString())
            editor.putString("ACCELEROMETERY", event!!.values[0].toString())
            editor.putString("ACCELEROMETERZ", event!!.values[0].toString())

        }else if(s.type == Sensor.TYPE_RELATIVE_HUMIDITY){

            editor.putString("HUMIDITY", event!!.values[0].toString())

        }else if(s.type == Sensor.TYPE_PRESSURE){

            editor.putString("AIRPRESSURE", event!!.values[0].toString())

        }


        editor.commit()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onResume() {
        super.onResume()

        //register sensor modules
        registerSensor()

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun initSensor(){

        //sensor manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //sensor modules
        TEMPERATURE = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        LIGHT = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        GYROSCOPE = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        ACCELEROMETER = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        HUMIDITY = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        AIR_PRESSURE = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        //register sensor modules
        registerSensor()

        //shared preference to provide data to evaluator class
        SENSOR_DATA = getSharedPreferences("SENSOR_DATA", Context.MODE_PRIVATE)

    }

    private fun registerSensor(){

        val editor = SENSOR_DATA!!.edit()

        //ambient temperature sensor
        if( TEMPERATURE != null){
            sensorManager.registerListener(this, TEMPERATURE, SensorManager.SENSOR_DELAY_NORMAL)
        }else{
            editor.putString("TEMPERATURE", "NA")
        }

        //proximity sensor
        if( LIGHT != null){
            sensorManager.registerListener(this, LIGHT, SensorManager.SENSOR_DELAY_NORMAL)
        }else{
            editor.putString("LIGHT", "NA")
        }

        //gyroscope
        if( GYROSCOPE != null){
            sensorManager.registerListener(this, GYROSCOPE, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            editor.putString("GYROSCOPE_X", "NA")
            editor.putString("GYROSCOPE_Y", "NA")
            editor.putString("GYROSCOPE_Z", "NA")
        }

        //accelerometer
        if( ACCELEROMETER != null){
            sensorManager.registerListener(this, ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL)
        }else{
            editor.putString("ACCELEROMETER_X", "NA")
            editor.putString("ACCELEROMETER_Y", "NA")
            editor.putString("ACCELEROMETER_Z", "NA")
        }

        //ambient humidity sensor
        if( HUMIDITY != null){
            sensorManager.registerListener(this, HUMIDITY, SensorManager.SENSOR_DELAY_NORMAL)
        }else {
            editor.putString("HUMIDITY","NA")
        }

        //air pressure sensor
        if( AIR_PRESSURE != null){
            sensorManager.registerListener(this, AIR_PRESSURE, SensorManager.SENSOR_DELAY_NORMAL)
        }else {
            editor.putString("AIR_PRESSURE", "NA")
        }


        editor.commit()

    }

}