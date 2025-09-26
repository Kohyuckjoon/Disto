package com.example.yscdisto.ble

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*

// 라이카 DISTO D5의 서비스/특성 UUID (예시, 실제 UUID로 교체 필요)
// 이 UUID는 DISTO D5의 매뉴얼, 개발자 문서, 또는 nRF Connect 같은 앱으로 찾아야 함.
val SERVICE_UUID: UUID = UUID.fromString("0000xxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000yyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy")

class DistoBleManager(
    private val context: Context,
    private val onDeviceFound: (BluetoothDevice) -> Unit,
    private val onConnectionStateChanged: (String) -> Unit
) {
    // BluetoothManagerCompat 대신 표준 BluetoothManager 사용
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null

    @Suppress("MissingPermission")
    fun startScan() {
        if (!checkPermissions()) {
            Log.e("DistoBleManager", "블루투스 권한 없음")
            return
        }
        val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        bluetoothLeScanner?.startScan(scanCallback)
    }

    @Suppress("MissingPermission")
    fun stopScan() {
        if (!checkPermissions()) return
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
    }

    @Suppress("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        if (!checkPermissions()) return
        stopScan()
        // BLE 연결은 GATT 연결을 사용합니다.
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val scanCallback = object : ScanCallback() {
        @Suppress("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                val device = it.device
                // 기기 이름으로 DISTO D5를 필터링
                if (device.name?.contains("DISTO D5") == true) {
                    onDeviceFound(device)
                    Log.d("DistoBleManager", "Disto D5 발견: ${device.name}")
                }
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    bluetoothGatt = gatt
                    onConnectionStateChanged("연결 성공")
                    Log.d("DistoBleManager", "GATT 연결 성공. 서비스 탐색 시작.")
                    @Suppress("MissingPermission")
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    onConnectionStateChanged("연결 끊김")
                    Log.d("DistoBleManager", "GATT 연결 끊김")

                    // ⭐ 이 부분에 권한 확인 코드를 추가해야 합니다.
                    if (checkPermissions()) {
                        gatt?.close()
                    } else {
                        Log.e("DistoBleManager", "블루투스 연결 끊김 시 권한 부족")
                    }
                }
            }
        }

        @Suppress("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt?.getService(SERVICE_UUID)
                service?.let {
                    val characteristic = it.getCharacteristic(CHARACTERISTIC_UUID)
                    characteristic?.let { char ->
                        // 데이터 수신을 위한 알림(Notification) 활성화
                        gatt.setCharacteristicNotification(char, true)
                        val descriptor = char.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                        Log.d("DistoBleManager", "측정값 Characteristic 알림 활성화")
                        onConnectionStateChanged("Disto D5 연결 및 준비 완료")
                    } ?: run {
                        Log.e("DistoBleManager", "Characteristic을 찾을 수 없습니다: $CHARACTERISTIC_UUID")
                    }
                } ?: run {
                    Log.e("DistoBleManager", "Service를 찾을 수 없습니다: $SERVICE_UUID")
                }
            } else {
                Log.e("DistoBleManager", "서비스 탐색 실패: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            // 측정값이 Characteristic을 통해 전송될 때 호출
            characteristic?.let {
                val value = it.getStringValue(0)
                Log.d("DistoBleManager", "받은 측정값: $value")
                // TODO: 이 값을 StartMeasurementFragment로 전달 (예: LiveData 또는 Flow 사용)
            }
        }
    }

    // 권한 확인 함수
    private fun checkPermissions(): Boolean {
        return context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }
}