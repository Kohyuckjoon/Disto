package com.example.yscdisto.ui.adapter

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.yscdisto.databinding.ItemDeviceBinding

class DeviceViewHolder(
    private val binding: ItemDeviceBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(device: BluetoothDevice, onClick: (BluetoothDevice) -> Unit) {
        val context = binding.root.context

        val deviceName = if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        ) {
            device.name ?: "Unknown Device"
        } else {
            "Permission Required"
        }

        val deviceAddress = if (
            ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        ) {
            device.address
        } else {
            "Permission Required"
        }

        binding.deviceName.text = deviceName
        binding.deviceAddress.text = deviceAddress

        binding.root.setOnClickListener { onClick(device) }
    }
}