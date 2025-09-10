package com.example.yscdisto

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yscdisto.databinding.ActivityStartMeasurementBinding

class StartMeasurement : AppCompatActivity() {
    private lateinit var databinding : ActivityStartMeasurementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = ActivityStartMeasurementBinding.inflate(layoutInflater)

        setContentView(databinding.root)

    }
}