package com.sonisoft.datasetu.feature.send

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.manager.TransferManager
import com.sonisoft.datasetu.manager.DeviceDiscoveryManager
import com.sonisoft.datasetu.manager.DiscoveredDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendViewModel @Inject constructor(
    private val transferManager: TransferManager,
    private val discoveryManager: DeviceDiscoveryManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SendUiState())
    val uiState: StateFlow<SendUiState> = _uiState.asStateFlow()
    
    val transferState = transferManager.transferState
    val discoveredDevices = discoveryManager.discoveredDevices
    
    private val filesToSend = transferManager.filesToSend

    init {
        discoveryManager.startDiscovery()
    }

    override fun onCleared() {
        discoveryManager.stopDiscovery()
        super.onCleared()
    }

    fun onQrScanned(ipAddress: String) {
        if (filesToSend.isNotEmpty() && ipAddress.isNotEmpty()) {
            _uiState.update { it.copy(scannedIp = ipAddress, isScanning = false) }
            viewModelScope.launch {
                transferManager.connectAndSendFiles(ipAddress, filesToSend)
            }
        }
    }

    fun connectToDevice(device: DiscoveredDevice) {
        if (filesToSend.isNotEmpty()) {
            _uiState.update { it.copy(scannedIp = device.ipAddress, isScanning = false) }
            viewModelScope.launch {
                transferManager.connectAndSendFiles(device.ipAddress, filesToSend)
            }
        }
    }

    fun resetAndSendMore(onNavigateBack: () -> Unit) {
        transferManager.resetState()
        onNavigateBack()
    }
}

data class SendUiState(
    val scannedIp: String = "",
    val isScanning: Boolean = true
)