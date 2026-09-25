package com.sonisoft.datasetu.manager

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import com.sonisoft.datasetu.data.remote.transfer.TransferProtocol
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceDiscoveryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    
    private val SERVICE_TYPE = "_datasetu._tcp."
    private var serviceName = "DataSetu_Device"

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    // Call this when entering the ReceiveScreen
    fun startBroadcasting(deviceName: String) {
        stopBroadcasting() // clear any existing broadcast
        
        serviceName = "DataSetu_$deviceName"

        val serviceInfo = NsdServiceInfo().apply {
            this.serviceName = this@DeviceDiscoveryManager.serviceName
            this.serviceType = SERVICE_TYPE
            this.port = TransferProtocol.DEFAULT_PORT
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                this@DeviceDiscoveryManager.serviceName = NsdServiceInfo.serviceName
            }
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
            override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
        }

        try {
            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopBroadcasting() {
        if (registrationListener != null) {
            try {
                nsdManager.unregisterService(registrationListener)
            } catch (e: Exception) {}
            registrationListener = null
        }
    }

    // Call this when entering the SendScreen
    fun startDiscovery() {
        stopDiscovery()
        _discoveredDevices.update { emptyList() }

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {}

            override fun onServiceFound(service: NsdServiceInfo) {
                if (service.serviceType == SERVICE_TYPE && service.serviceName.startsWith("DataSetu_")) {
                    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            val hostAddress = serviceInfo.host.hostAddress
                            if (hostAddress != null) {
                                val deviceName = serviceInfo.serviceName.removePrefix("DataSetu_")
                                val device = DiscoveredDevice(deviceName, hostAddress, serviceInfo.port)
                                
                                _discoveredDevices.update { current ->
                                    if (current.none { it.ipAddress == device.ipAddress }) {
                                        current + device
                                    } else current
                                }
                            }
                        }
                    })
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                val lostName = service.serviceName.removePrefix("DataSetu_")
                _discoveredDevices.update { current ->
                    current.filterNot { it.name == lostName }
                }
            }

            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                stopDiscovery()
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                stopDiscovery()
            }
        }

        try {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopDiscovery() {
        if (discoveryListener != null) {
            try {
                nsdManager.stopServiceDiscovery(discoveryListener)
            } catch (e: Exception) {}
            discoveryListener = null
        }
    }
}

data class DiscoveredDevice(
    val name: String,
    val ipAddress: String,
    val port: Int
)