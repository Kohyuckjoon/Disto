package com.example.yscdisto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
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
        dataBinding.mcBtn4.setOnClickListener {

        }

        dataBinding.mcBtn1.setOnClickListener {
//            val intent = Intent(this, DistoConnection::class.java)
//            startActivity(intent)
            supportFragmentManager.commit {
                replace(R.id.fl_disto_conn, DistoConnectFragment())
                addToBackStack(null)
            }
        }

        dataBinding.mcBtn4.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_start_measurement, StartMeasurementFragment())
                addToBackStack(null)
                Log.e("khj", "test_01");
            }
        }

//        dataBinding.mcBtn4.setOnClickListener {
//            val intent = Intent(this, StartMeasurement::class.java)
//            startActivity(intent)
//        }
    }
}