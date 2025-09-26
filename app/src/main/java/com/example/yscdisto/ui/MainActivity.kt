package com.example.yscdisto.ui

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.commit
import com.example.yscdisto.DistoCommandManager
import com.example.yscdisto.ui.disto.CameraSearchListFragment
import com.example.yscdisto.ui.disto.ProjectCreateFragment
import com.example.yscdisto.ui.disto.ProjectSelectFragment
import com.example.yscdisto.ui.disto.DistoConnectFragment
import com.example.yscdisto.R
import com.example.yscdisto.data.model.ProjectCreate
import com.example.yscdisto.ui.disto.StartMeasurementFragment
import com.example.yscdisto.databinding.ActivityMainBinding
import com.example.yscdisto.ui.disto.MeasurementListFragment

class MainActivity : AppCompatActivity(), ProjectSelectFragment.OnProjectSelectedListener, DistoCommandManager {
    private lateinit var viewBinding : ActivityMainBinding

    private var writeCharacteristic: BluetoothGattCharacteristic? = null // 연결된 Disto 장비의 Write Characteristic을 저장하는 변수
    private var currentGatt: BluetoothGatt? = null // 연결된 GATT 객체도 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.mcProjectCreate.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_project_create, ProjectCreateFragment())
                addToBackStack(null)
            }
        }

        viewBinding.mcProjectSelect.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fl_project_select, ProjectSelectFragment())
                addToBackStack(null)
            }
        }

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

        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }

    override fun onProjectSelected(project: ProjectCreate) {
        viewBinding.mcProjectStatus.text = "프로젝트명 : ${project.name}"
        Log.e("khj", "선택된 프로젝트(MainActivity) : $project")

        supportFragmentManager.popBackStack()
    }

    @Suppress("MissingPermission")
    override fun sendMeasurementCommand(commandData: ByteArray): Boolean {
        val characteristic = writeCharacteristic
        val gatt = currentGatt

        if (gatt == null || characteristic == null ) {
            Log.e("BLE_CMD", "GATT 객체 또는 Write Characteristic이 준비되지 않았습니다.")
            return false
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BLE_CMD", "BLUETOOTH_CONNECT 권한 없음")
            return false
        }

        characteristic.value = commandData

        val success = gatt.writeCharacteristic(characteristic)
        return success
    }

    @Suppress("MissingPermission")
    override fun isDistoConnected(): Boolean {
        return currentGatt != null &&
                getSystemService(Context.BLUETOOTH_SERVICE)?.let { it as BluetoothManager }?.getConnectionState(currentGatt?.device, BluetoothProfile.GATT) == BluetoothProfile.STATE_CONNECTED
    }

    fun setDistoGatt(gatt: BluetoothGatt, writeChar: BluetoothGattCharacteristic) {
        this.currentGatt = gatt
        this.writeCharacteristic = writeChar
    }
}