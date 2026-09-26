package com.sonisoft.datasetu.feature.receive

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.core.datastore.UserPreferences
import com.sonisoft.datasetu.core.util.NetworkUtil
import com.sonisoft.datasetu.manager.DeviceDiscoveryManager
import com.sonisoft.datasetu.manager.TransferManager
import com.sonisoft.datasetu.manager.TransferStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transferManager: TransferManager,
    private val discoveryManager: DeviceDiscoveryManager,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiveUiState())
    val uiState: StateFlow<ReceiveUiState> = _uiState.asStateFlow()
    
    val transferState = transferManager.transferState

    init {
        refreshQrCodeAndServer()
    }

    private fun startBroadcasting() {
        viewModelScope.launch {
            val session = userPreferences.userSession.first()
            val deviceName = session.deviceName.ifEmpty { "DataSetuDevice" }
            discoveryManager.startBroadcasting(deviceName)
        }
    }

    fun refreshQrCodeAndServer() {
        generateQrCodeContent()
        if (transferState.value.status == TransferStatus.IDLE || transferState.value.status == TransferStatus.ERROR) {
            startServer()
            startBroadcasting()
        }
    }

    fun resetAndReceiveMore() {
        transferManager.resetState()
        refreshQrCodeAndServer()
    }

    override fun onCleared() {
        super.onCleared()
        discoveryManager.stopBroadcasting()
    }

    private fun generateQrCodeContent() {
        val ipString = NetworkUtil.getLocalIpAddress(context)
        _uiState.update { it.copy(qrContent = ipString) }
    }

    private fun startServer() {
        viewModelScope.launch {
            transferManager.startServerAndReceiveFiles()
        }
    }
}

data class ReceiveUiState(
    val qrContent: String = ""
)
