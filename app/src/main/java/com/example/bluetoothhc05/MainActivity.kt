package com.example.bluetoothhc05

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var threadForBluetooth: ThreadForBluetooth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            requestBluetoothOn()
        } else {
            initBluetooth()
        }
    }

    private fun requestBluetoothOn() {
        val check = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startForResultBluetoothOn.launch(check)
    }

    private val startForResultBluetoothOn = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            initBluetooth()
        } else {
            Log.d("TAGt", "Bluetooth не включен!!!")
        }
    }

    private fun initBluetooth() {
        threadForBluetooth = ThreadForBluetooth(bluetoothAdapter) { inputString ->
            Log.d("TAGt", "inputString = $inputString")
        }
        threadForBluetooth.start()
    }
}