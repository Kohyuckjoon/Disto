package com.example.yscdisto

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yscdisto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var dataBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        // Test Code
        /*dataBinding.distoConnention.setOnClickListener {
            val intent = Intent(this, DistoConnection::class.java)
            startActivity(intent)
        }*/

        dataBinding.mcBtn1.setOnClickListener {
            val intent = Intent(this, DistoConnection::class.java)
            startActivity(intent)
        }

        dataBinding.mcBtn4.setOnClickListener {
            val intent = Intent(this, StartMeasurement::class.java)
            startActivity(intent)
        }
    }
}