package com.sonisoft.datasetu.core.permission

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher

data class ConnectivityState(
    val isWifiOn: Boolean = false,
    val isBluetoothOn: Boolean = false,
    val isLocationOn: Boolean = false
) {
    val isAllRequiredOn: Boolean
        get() = (isWifiOn || isBluetoothOn) && isLocationOn
}

object ConnectivitySettingsManager {

    fun getConnectivityState(context: Context): ConnectivityState {
        return ConnectivityState(
            isWifiOn = isWifiEnabled(context),
            isBluetoothOn = isBluetoothEnabled(context),
            isLocationOn = isLocationEnabled(context)
        )
    }

    fun isWifiEnabled(context: Context): Boolean {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wifiManager?.isWifiEnabled == true
        } catch (e: Exception) {
            false
        }
    }

    fun isBluetoothEnabled(context: Context): Boolean {
        return try {
            val bluetoothManager = context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            bluetoothManager?.adapter?.isEnabled == true
        } catch (e: Exception) {
            false
        }
    }

    fun isLocationEnabled(context: Context): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
        } catch (e: Exception) {
            false
        }
    }

    fun openWifiSettings(context: Context) {
        try {
            val intent = Intent("android.settings.panel.action.WIFI")
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun requestEnableBluetooth(context: Context, launcher: ActivityResultLauncher<Intent>?) {
        try {
            if (launcher != null) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                launcher.launch(intent)
            } else {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun openLocationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_SETTINGS)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun openHotspotSettings(context: Context) {
        try {
            val intent = Intent().apply {
                setClassName("com.android.settings", "com.android.settings.TetherSettings")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(intent)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
