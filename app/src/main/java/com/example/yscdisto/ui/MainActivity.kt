package com.example.yscdisto.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.yscdisto.CameraSearchListFragment
import com.example.yscdisto.ProjectCreateFragment
import com.example.yscdisto.ProjectSelectFragment
import com.example.yscdisto.ui.disto.DistoConnectFragment
import com.example.yscdisto.R
import com.example.yscdisto.ui.disto.StartMeasurementFragment
import com.example.yscdisto.databinding.ActivityMainBinding
import com.example.yscdisto.ui.disto.MeasurementListFragment

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)



        viewBinding.mcDistioConn.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_disto_conn, DistoConnectFragment())
                addToBackStack(null)
            }
        }

        viewBinding.mcCameraConn.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_camera_conn, CameraSearchListFragment())
                addToBackStack(null)
            }
        }

        viewBinding.mcMeasurementList.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_measurement_list, MeasurementListFragment())
                addToBackStack(null)
            }
        }

        viewBinding.mcMeasurement.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_start_measurement, StartMeasurementFragment())
                addToBackStack(null)
                Log.e("khj", "test_01");
            }
        }

        viewBinding.mcProjectCreate.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_project_create, ProjectCreateFragment())
            }
        }

        viewBinding.mcProjectSelect.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_project_select, ProjectSelectFragment())
            }
        }
    }
}