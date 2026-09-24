package com.sonisoft.datasetu.feature.receive

import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.manager.TransferManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transferManager: TransferManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReceiveUiState())
    val uiState: StateFlow<ReceiveUiState> = _uiState.asStateFlow()
    
    val transferState = transferManager.transferState

    init {
        generateQrCodeContent()
        startServer()
    }

    private fun generateQrCodeContent() {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        val ipString = String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
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