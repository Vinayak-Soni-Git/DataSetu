package com.sonisoft.datasetu.core.util

import android.content.Context
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface

object NetworkUtil {

    fun getLocalIpAddress(context: Context): String {
        // 1. Try WifiManager first for active Wi-Fi connection
        val wifiIp = getWifiManagerIp(context)
        if (wifiIp.isNotEmpty() && wifiIp != "0.0.0.0") {
            return wifiIp
        }

        // 2. Iterate NetworkInterfaces (for Wi-Fi Direct, Hotspot, AP, or Ethernet)
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return "0.0.0.0"
            for (networkInterface in interfaces.toList()) {
                if (!networkInterface.isUp || networkInterface.isLoopback) continue

                val interfaceName = networkInterface.name.lowercase()
                for (inetAddress in networkInterface.inetAddresses.toList()) {
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        val hostAddress = inetAddress.hostAddress
                        if (!hostAddress.isNullOrEmpty() && hostAddress != "127.0.0.1" && hostAddress != "0.0.0.0") {
                            if (interfaceName.contains("wlan") || interfaceName.contains("p2p") || interfaceName.contains("ap") || interfaceName.contains("swlan") || interfaceName.contains("eth")) {
                                return hostAddress
                            }
                        }
                    }
                }
            }

            // 3. Fallback to any valid non-loopback IPv4 address
            for (networkInterface in interfaces.toList()) {
                if (!networkInterface.isUp || networkInterface.isLoopback) continue
                for (inetAddress in networkInterface.inetAddresses.toList()) {
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        val hostAddress = inetAddress.hostAddress
                        if (!hostAddress.isNullOrEmpty() && hostAddress != "127.0.0.1" && hostAddress != "0.0.0.0") {
                            return hostAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "0.0.0.0"
    }

    private fun getWifiManagerIp(context: Context): String {
        return try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val ipAddress = wifiManager?.connectionInfo?.ipAddress ?: 0
            if (ipAddress != 0) {
                String.format(
                    "%d.%d.%d.%d",
                    ipAddress and 0xff,
                    ipAddress shr 8 and 0xff,
                    ipAddress shr 16 and 0xff,
                    ipAddress shr 24 and 0xff
                )
            } else {
                "0.0.0.0"
            }
        } catch (e: Exception) {
            "0.0.0.0"
        }
    }

    fun extractIpAddress(rawInput: String): String {
        val clean = rawInput.trim()
            .removePrefix("http://")
            .removePrefix("https://")
        val host = clean.split(":").firstOrNull() ?: clean
        return host.trim()
    }
}
