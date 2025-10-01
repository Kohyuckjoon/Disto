package com.example.yscdisto.ui.adapter.kotlin

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yscdisto.ui.adapter.DeviceViewHolder
import com.example.yscdisto.databinding.ItemDeviceBinding

class DeviceAdapter (
    private val devices: MutableList<BluetoothDevice>,
    private val onClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position], onClick)
    }

    override fun getItemCount(): Int = devices.size

    fun addDevice(device: BluetoothDevice) {
        devices.add(device)
        notifyItemInserted(devices.size - 1)
    }

    fun setDevices(newDevices: List<BluetoothDevice>) {
        devices.clear()
        devices.addAll(newDevices)
        notifyDataSetChanged()
    }

    fun clearDevices() {
        devices.clear()
        notifyDataSetChanged()
    }

//    fun addDevice(device: BluetoothDevice) {
//        if (devices.none { it.address == device.address }) {
//            devices.add(device)
////            notifyItemInserted(devices.size - 1)
//            notifyDataSetChanged()
//        }
//    }

//    fun updateDevices(newDevices: List<BluetoothDevice>) {
//        devices.clear()
//        devices.addAll(newDevices)
//        notifyDataSetChanged()
//    }
}