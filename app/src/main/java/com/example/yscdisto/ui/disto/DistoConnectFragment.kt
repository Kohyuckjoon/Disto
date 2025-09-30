package com.example.yscdisto.ui.disto

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ch.leica.sdk.Devices.Device
import com.example.yscdisto.DistoCommandManager
import com.example.yscdisto.ui.adapter.DeviceAdapter
import com.example.yscdisto.databinding.FragmentDistoConnectBinding
import com.example.yscdisto.ui.MainActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * Fragment Bluetooth
 */
class DistoConnectFragment : Fragment(){
    private lateinit var binding: FragmentDistoConnectBinding

    // 💡 변경 1: Context를 사용하지 않고, 늦게 초기화되도록 var로 변경
    private var bluetoothAdapter: BluetoothAdapter? = null

    // 💡 변경 2: Adapter가 초기화될 때까지 null 유지
    private var bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    // 스캔 결과를 담을 리스트와 어댑터
    private val deviceList = mutableListOf<BluetoothDevice>()
    private lateinit var adapter: DeviceAdapter

    // GATT 연결 객체
    private var bluetoothGatt: BluetoothGatt? = null

    // 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            // 초기화된 후 스캔 시작
            startBleScan()
        } else {
            Log.e("BLE_KHJ", "필요한 블루투스 권한이 허용되지 않았습니다. 스캔 불가.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDistoConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🚀 변경 3: onViewCreated에서 Context를 사용하여 초기화 수행
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner


        // RecyclerView 설정
        adapter = DeviceAdapter(deviceList) { device ->
            stopBleScan()
            showConnetDialog(device)
//            connectToDevice(device)
        }
        binding.recyclerViewDevices.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDevices.adapter = adapter

        // 권한 확인 및 스캔 시작
        checkPermissions()
    }

    @Suppress
    private fun showConnetDialog(device: BluetoothDevice) {
        val deviceName = if (checkBluetoothConnectPermission()) {
            device.name ?: "Unknown Device"
        } else {
            "Unknown Device (Permission required)"
        }

        // AlertDialog Builder를 사용해서 다이얼로그 생성
        context?.let { safeContext ->
            android.app.AlertDialog.Builder(safeContext)
                .setTitle("기기 연결")
                .setMessage("'$deviceName' 기기에 연결하시겠습니까? \n주소: ${device.address}")
                .setPositiveButton("확인") { dialog, _ ->
                    // 확인 버튼 클릭시 연결 로직 호출
                    connectToDevice(device)
                    dialog.dismiss()
                }
                .setNegativeButton("취소") { dialog, _ ->
                    // 취소 버튼 클릭 시 스캔을 다시할 수 있도록 허용
                    startBleScan()
                    dialog.cancel()
                }
                .show()
        }
    }

    // 권한 확인 유틸리티 함수
    private fun checkBluetoothConnectPermission(): Boolean {
        // 💡 requireContext() 대신 context?를 사용하여 null이 아닐 때만 권한 확인을 수행
        val safeContext = context ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(safeContext, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            // S 미만 버전에서는 BLUETOOTH_CONNECT 권한이 필요하지 않음
            // 하지만 context가 없으면 당연히 권한 체크도 불가하므로 false를 리턴하는 것이 안전함.
            true
        }
    }

    private fun checkPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        requestPermissionLauncher.launch(permissions)
    }

    @Suppress("MissingPermission")
    private fun startBleScan() {
        // null 체크를 통해 초기화 여부 확인
        if (bluetoothAdapter?.isEnabled == false) {
            Log.e("BLE", "블루투스가 활성화 되어 있지 않습니다.")
            return
        }

        // BLUETOOTH_SCAN 권한이 실제로 있는지 최종 확인 (API 31 이상)
        val scanPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            // S 미만은 ACCESS_FINE_LOCATION으로 스캔
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        if (!scanPermission) {
            Log.e("BLE_KHJ", "스캔 권한이 없습니다. 스캔 시작 불가.")
            return
        }

        Log.e("BLE_KHJ", "BLE 스캔 시작...")
        adapter.clearDevices()
        // bluetoothLeScanner가 null인지 다시 확인
        bluetoothLeScanner?.startScan(bleScanCallback)
        // binding.progressBar.visibility = View.VISIBLE
    }

    @Suppress("MissingPermission")
    private fun stopBleScan() {
        Log.e("BLE_KHJ", "BLE 스캔 중지...")
        // BLUETOOTH_SCAN 권한 체크
        val scanPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        if (scanPermission) {
            bluetoothLeScanner?.stopScan(bleScanCallback)
        } else {
            Log.e("BLE_KHJ", "스캔 중지 권한 부족. 강제 종료 시도 안함.")
        }
        // binding.progressBar.visibility = View.GONE
    }

    // BLE 스캔 콜백
    private val bleScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                // onScanResult 내부에서 device.name에 접근하기 전에 권한 확인
                if (!checkBluetoothConnectPermission()) {
                    Log.w("BLE_KHJ", "BLUETOOTH_CONNECT 권한 부족. 기기 이름 접근 불가.")
                    // 권한이 없으면 이름으로 필터링하지 않고, 주소로만 처리하거나 스킵
                    return
                }

                // 기기 이름이 "DISTO"를 포함하는 경우에만 추가
//                if (device.name?.contains("DISTO") == true) {
                    // 중복 방지
                    if (!deviceList.any { it.address == device.address }) {
                        deviceList.add(device)
                        // UI 업데이트는 메인 스레드에서
                        requireActivity().runOnUiThread {
                            adapter.notifyItemInserted(deviceList.size - 1)
                        }
                        Log.e("BLE_KHJ", "발견된 BLE 기기 : ${device.name}, 주소 : ${device.address}")
                    }
//                }
            }
        }

        override fun onBatchScanResults(results: List<ScanResult?>?) {
            super.onBatchScanResults(results)
            // Batch 결과를 처리할 때도 onScanResult를 통해 처리하면 권한 체크 로직 공유
            results?.forEach { onScanResult(0, it) }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e("BLE_KHJ", "스캔 실패 : $errorCode")
        }
    }

    @Suppress("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        // connectGatt 호출 전에 BLUETOOTH_CONNECT 권한 최종 확인
        if (!checkBluetoothConnectPermission()) {
            Log.e("BLE_KHJ", "연결 권한이 없어 connectGatt 호출 불가.")
            return
        }

        Log.e("BLE_KHJ", "연결 시도 : ${device.name}")
        // 연결하려는 기기에 대한 GATT 콜백 정의
        device.connectGatt(requireActivity(), false, gattCallback)
    }

    // GATT 콜백 정의
    private val gattCallback = object : BluetoothGattCallback () {
        @Suppress("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.e("BLE_KHJ", "GATT 연결 성공. 서비스 탐색 시작...")
                bluetoothGatt = gatt
                // BLUETOOTH_CONNECT 권한 필요
                if (checkBluetoothConnectPermission()) {
                    gatt?.discoverServices()
                } else {
                    Log.e("BLE_KHJ", "BLUETOOTH_CONNECT 권한 부족으로 서비스 탐색 불가.")
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e("BLE_KHJ", "GATT 연결 해제.")
                bluetoothGatt = null
            }
        }

        @Suppress("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("BLE_KHJ", "서비스 탐색 완료")
                // TODO: Disto 기기의 UUID를 확인하여 서비스와 특성을 찾고 데이터 읽기/쓰기 구현
                // 🚀 1. 찾은 Disto D5 UUID 적용
                val distoServiceUuid = "3ab10100-f831-4395-b29d-570977d5bf94" // Disto 고유 서비스

                // 🚀 2. 측정값 수신용 Notify Characteristic 적용
                val notifyCharacteristicUuid = "3ab10111-f831-4395-b29d-570977d5bf94"

                // 🚀 3. 기기 명령 전송용 Write Characteristic 적용 (측정 시작 등에 사용)
                val writeCharacteristicUuid = "3ab10120-f831-4395-b29d-570977d5bf94"

                val cccdUuid = "00002902-0000-1000-8000-00805f9b34fb" // 고정 (0x2902)

                if (!checkBluetoothConnectPermission()) {
                    Log.e("BLE_KHJ", "BLUETOOTH_CONNECT 권한 부족으로 Characteristic 접근 불가.")
                    return
                }

                // A. 데이터 수신 (Notify) 특성 설정
                val notifyService = gatt?.getService(java.util.UUID.fromString(distoServiceUuid))
                val notifyCharacteristic = notifyService?.getCharacteristic(java.util.UUID.fromString(notifyCharacteristicUuid))

                notifyCharacteristic?. let {
                    Log.d("BLE_KHJ", "Notify Characteristic 찾음. 알림 활성화 시도.")
                    // 알림 활성화(Notify)
                    gatt.setCharacteristicNotification(it, true)

                    // CCCD 디스크립터를 찾아서 ENABLE_NOTIFICATION_VALUE 쓰기
                    val descriptor = it.getDescriptor(java.util.UUID.fromString(cccdUuid))
                    descriptor?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    gatt.writeDescriptor(descriptor)
                }

                // B. 명령 전송 (Write) 특성 설정 (필요 시)
                val writeService = gatt?.getService(java.util.UUID.fromString(distoServiceUuid))
                val writeCharacteristic = writeService?.getCharacteristic(java.util.UUID.fromString(writeCharacteristicUuid))

                // writeCharacteristic 객체를 저장하여 나중에 측정 명령을 보낼 때 사용합니다.
                if (gatt != null && writeCharacteristic != null) {
                    // 💡 추가: Activity에 GATT 및 Write Characteristic 객체 전달
                    (activity as? DistoCommandManager)?.let {
                        // Activity에 Characteristic을 저장하도록 요청
                        (it as? MainActivity)?.setDistoGatt(gatt, writeCharacteristic)
                    }
                }
                // TODO: 이 객체를 ViewModel 또는 Connection Manager에 저장하세요.

            } else {
                Log.e("BLE_KHJ", "서비스 탐색 실패 : $status")
            }
        }

        @Suppress("MissingPermission")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            // Disto 기기에서 데이터가 변경되어 알림이 오는 경우
            val data = characteristic.value?.let { String(it) } // null 체크 수정
            Log.e("BLE_KHJ", "데이터 수신: $data")
            // TODO : Ui 업데이트 (UI스레드에서)
            requireActivity().runOnUiThread {
                // 측정 결과를 화면에 표시하는 로직
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopBleScan()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트 종료시 GATT 연결 해제
        if (bluetoothGatt != null) {
            @Suppress("MissingPermission")
            // 연결 해제 전에 권한이 있는지 확인하여 SecurityException을 방지
            // Note: 이 시점에는 requireContext() 대신 Context를 사용할 수 있는지 확인하는 것이 더 안전함.
            if (context != null && checkBluetoothConnectPermission()) {
                bluetoothGatt?.close()
            }
            bluetoothGatt = null
        }
    }
}