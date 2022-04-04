package com.example.bluetoothhc05

import android.bluetooth.BluetoothAdapter
import android.util.Log
import java.io.IOException
import java.io.OutputStream
import java.util.*

class ThreadForBluetooth(
    private val bluetoothAdapter: BluetoothAdapter,
    private val readStringCallback: (String) -> Unit
) : Thread() {
    private var outputStream: OutputStream? = null
    override fun run() {
        try {
            val deviceBT = bluetoothAdapter.bondedDevices.find { it.name.contains("HC-") }
            deviceBT?.let {
                //https://developer.android.com/reference/android/bluetooth/BluetoothDevice#createRfcommSocketToServiceRecord(java.util.UUID)
                //Hint: If you are connecting to a Bluetooth serial board then try using the well-known SPP UUID 00001101-0000-1000-8000-00805F9B34FB.
                val bluetoothSocket = it.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                bluetoothSocket.connect()

                val inputStream = bluetoothSocket.inputStream
                outputStream = bluetoothSocket.outputStream
                val buffer = ByteArray(1)
                var inputString = ""
                while (true) {
                    inputStream.read(buffer)
                    inputString += String(buffer, 0, 1)
                    if (inputString.contains("\r\n")) {
                        readStringCallback(inputString)
                        inputString = ""
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.d("TAGt", "error SecurityException = $e")
            return
        } catch (e: IOException) {
            Log.d("TAGt", "error IOException = $e")
        }
    }

    fun write(s: String) {
        outputStream?.let {
            val bytesToSend = s.toByteArray()
            it.write(bytesToSend)
        }
    }
}