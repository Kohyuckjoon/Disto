package com.example.yscdisto.ui.disto

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yscdisto.ble.DistoBleManager // 3번에서 만들 BleManager
import kotlinx.coroutines.launch

class DistoConnectViewModel : ViewModel() {

    private val _scanResults = MutableLiveData<List<BluetoothDevice>>()
    val scanResults: LiveData<List<BluetoothDevice>> = _scanResults

    private val _connectionState = MutableLiveData<String>()
    val connectionState: LiveData<String> = _connectionState

    // BleManager 인스턴스 (Context가 필요하므로 초기화 시 주의)
    private var distoBleManager: DistoBleManager? = null

    // Fragment에서 context를 전달받아 BleManager 초기화
    fun initialize(context: Context) {
        if (distoBleManager == null) {
            distoBleManager = DistoBleManager(context.applicationContext, { device ->
                // 스캔 결과 콜백
                val currentList = _scanResults.value.orEmpty().toMutableList()
                if (currentList.none { it.address == device.address }) {
                    currentList.add(device)
                    _scanResults.postValue(currentList)
                }
            }, { state ->
                // 연결 상태 콜백
                _connectionState.postValue(state)
            })
        }
    }

    fun startScan() {
        _scanResults.value = emptyList()
        distoBleManager?.startScan()
    }

    fun connectToDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            distoBleManager?.connectToDevice(device)
        }
    }

    override fun onCleared() {
        super.onCleared()
        distoBleManager?.stopScan()
    }
}