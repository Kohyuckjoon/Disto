package com.example.yscdisto

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yscdisto.databinding.ActivityDistoConnectionBinding
import com.example.yscdisto.ui.adapter.DeviceAdapter

/**
 * Activity Bluetooth
 */
class DistoConnection : AppCompatActivity() {
    private lateinit var dataBinding: ActivityDistoConnectionBinding
    private lateinit var adapter: DeviceAdapter
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val deviceList = mutableListOf<BluetoothDevice>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 권한 결과 처리
        if(permissions.values.all { it }) {
            scanDevices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("khj", "test_run_A");
//        dataBinding = ActivityDistoConnectionBinding.inflate(layoutInflater)
//        setContentView(dataBinding.root)
//
//        adapter = DeviceAdapter(mutableListOf()) { device ->
//            connectToDevice(device)
//        }
//
//        dataBinding.recyclerViewDevices.layoutManager = LinearLayoutManager(this)
//        dataBinding.recyclerViewDevices.adapter = adapter
//
//        checkPermissionsAndScan() //Bluetooth Scan
    }

    private fun checkPermissionsAndScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )
        }
    }

    @SuppressLint("MissingPermission") // Lint 경고 방지
    private fun scanDevices() {
        if (bluetoothAdapter == null) {
            Log.e("Bluetooth", "블루투스를 지원하지 않는 기기입니다.")
            return
        }

        deviceList.clear()
        adapter.clearDevices()
//        adapter.updateDevices(deviceList)

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

            device?.let {
                if (it.bondState != BluetoothDevice.BOND_BONDED &&
                    deviceList.none { d -> d.address == it.address }
                ) {
                    deviceList.add(it)
                    adapter.addDevice(it)
                    Log.e("Bluetooth", "발견됨 : ${it.name} - ${it.address}")
                }
            }
        }
    }



    private fun connectToDevice(device: BluetoothDevice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                device.javaClass.getMethod("createBond").invoke(device)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        } catch (_: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("khj", "android lifeCycle test ----> onResume 호출")
    }

    override fun onStart() {
        super.onStart()
        Log.e("khj", "android lifeCycle test ----> onStart 호출")
    }

    override fun onPause() {
        super.onPause()
        Log.e("khj", "android lifeCycle test ----> onPause 호출")
    }

    override fun onStop() {
        super.onStop()
        Log.e("khj", "android lifeCycle test ----> onStop 호출")
    }
}