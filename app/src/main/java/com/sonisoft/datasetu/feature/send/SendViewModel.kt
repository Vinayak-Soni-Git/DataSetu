package com.sonisoft.datasetu.feature.send

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val transferManager: TransferManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SendUiState())
    val uiState: StateFlow<SendUiState> = _uiState.asStateFlow()
    
    val transferState = transferManager.transferState

    fun onFilesSelected(files: List<Uri>) {
        _uiState.update { it.copy(selectedFiles = files) }
    }

    fun onQrScanned(ipAddress: String) {
        val files = _uiState.value.selectedFiles
        if (files.isNotEmpty() && ipAddress.isNotEmpty()) {
            _uiState.update { it.copy(scannedIp = ipAddress, isScanning = false) }
            viewModelScope.launch {
                transferManager.connectAndSendFiles(ipAddress, files)
            }
        }
    }
}

data class SendUiState(
    val selectedFiles: List<Uri> = emptyList(),
    val scannedIp: String = "",
    val isScanning: Boolean = true
)