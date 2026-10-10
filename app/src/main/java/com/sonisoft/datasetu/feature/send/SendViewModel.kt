package com.sonisoft.datasetu.feature.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.core.util.NetworkUtil
import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.manager.DeviceDiscoveryManager
import com.sonisoft.datasetu.manager.DiscoveredDevice
import com.sonisoft.datasetu.manager.TransferManager
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
    
    val filesToSend: List<FileItem>
        get() = transferManager.filesToSend

    init {
        discoveryManager.startDiscovery()
        updateFilesCount()
    }

    fun updateFilesCount() {
        _uiState.update { it.copy(selectedFilesCount = filesToSend.size) }
    }

    override fun onCleared() {
        discoveryManager.stopDiscovery()
        super.onCleared()
    }

    fun onQrScanned(rawIpAddress: String) {
        val cleanIp = NetworkUtil.extractIpAddress(rawIpAddress)
        if (cleanIp.isEmpty() || cleanIp == "0.0.0.0") {
            _uiState.update { 
                it.copy(errorMessage = "Invalid IP address ($rawIpAddress). Make sure Receiver device is connected to Wi-Fi.")
            }
            return
        }

        _uiState.update { it.copy(scannedIp = cleanIp, isScanning = false, errorMessage = null) }

        if (filesToSend.isNotEmpty()) {
            viewModelScope.launch {
                transferManager.connectAndSendFiles(cleanIp, filesToSend)
            }
        } else {
            _uiState.update { it.copy(requiresFileSelection = true) }
        }
    }

    fun connectToDevice(device: DiscoveredDevice) {
        onQrScanned(device.ipAddress)
    }

    fun onFilesSelectedAndProceed() {
        updateFilesCount()
        val currentIp = _uiState.value.scannedIp
        if (currentIp.isNotEmpty() && filesToSend.isNotEmpty()) {
            _uiState.update { it.copy(requiresFileSelection = false) }
            
            // Do not restart if already transferring or completed
            val currentStatus = transferState.value.status
            if (currentStatus == com.sonisoft.datasetu.manager.TransferStatus.TRANSFERRING || 
                currentStatus == com.sonisoft.datasetu.manager.TransferStatus.CONNECTING ||
                currentStatus == com.sonisoft.datasetu.manager.TransferStatus.COMPLETED) {
                return
            }

            viewModelScope.launch {
                transferManager.connectAndSendFiles(currentIp, filesToSend)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetAndSendMore(onNavigateBack: () -> Unit) {
        transferManager.resetState()
        onNavigateBack()
    }
}

data class SendUiState(
    val scannedIp: String = "",
    val isScanning: Boolean = true,
    val selectedFilesCount: Int = 0,
    val requiresFileSelection: Boolean = false,
    val errorMessage: String? = null
)
