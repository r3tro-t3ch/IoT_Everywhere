package com.vishnujoshi.ioteverywhere

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.widget.addTextChangedListener
import com.vishnujoshi.ioteverywhere.compiler.compiler
import com.vishnujoshi.ioteverywhere.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private final val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        binding.run.setOnClickListener{

            val code = binding.codeEditor.text.trim().toString()

            Log.e(TAG,"${code}")

            val c = compiler(code + "\u0000")
            c.compile()

        }



    }
}