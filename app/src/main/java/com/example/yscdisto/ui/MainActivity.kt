package com.example.yscdisto.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.yscdisto.CameraSearchListFragment
import com.example.yscdisto.ui.disto.DistoConnectFragment
import com.example.yscdisto.R
import com.example.yscdisto.ui.disto.StartMeasurementFragment
import com.example.yscdisto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var dataBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        dataBinding.mcDistioConn.setOnClickListener {
//            val intent = Intent(this, DistoConnection::class.java)
//            startActivity(intent)
            supportFragmentManager.commit {
                replace(R.id.fl_disto_conn, DistoConnectFragment())
                addToBackStack(null)
            }
        }

        dataBinding.mcCameraConn.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_camera_conn, CameraSearchListFragment())
                addToBackStack(null)
            }
        }

        dataBinding.mcMeasurementList.setOnClickListener {

        }

        dataBinding.mcMeasurement.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_start_measurement, StartMeasurementFragment())
                addToBackStack(null)
                Log.e("khj", "test_01");
            }
        }
    }
}