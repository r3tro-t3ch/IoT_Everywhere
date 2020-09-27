package com.vishnujoshi.ioteverywhere

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi

import com.vishnujoshi.ioteverywhere.compiler.compiler
import com.vishnujoshi.ioteverywhere.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private final val TAG = "MainActivity"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //console = outputScreen

        binding.run.setOnClickListener{

            val code = binding.codeEditor.text.trim().toString()

            //Log.e(TAG,"${code}")

            //binding.outputScreen.visibility = View.VISIBLE
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

}