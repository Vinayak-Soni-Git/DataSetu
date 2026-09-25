package com.sonisoft.datasetu.manager

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.sonisoft.datasetu.data.remote.transfer.TransferProtocol
import com.sonisoft.datasetu.data.remote.transfer.TransferSocket
import com.sonisoft.datasetu.domain.model.FileItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import java.io.File
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransferManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var serverSocket: ServerSocket? = null
    private var clientSocket: TransferSocket? = null
    
    var filesToSend: List<FileItem> = emptyList()
    
    private val _transferState = MutableStateFlow(TransferState())
    val transferState: StateFlow<TransferState> = _transferState.asStateFlow()

    fun resetState() {
        _transferState.value = TransferState()
        closeConnections()
    }

    suspend fun startServerAndReceiveFiles() = withContext(Dispatchers.IO) {
        try {
            _transferState.update { it.copy(status = TransferStatus.CONNECTING) }
            serverSocket = ServerSocket(TransferProtocol.DEFAULT_PORT)
            
            val rawSocket = serverSocket!!.accept()
            clientSocket = TransferSocket(rawSocket)
            _transferState.update { it.copy(status = TransferStatus.TRANSFERRING) }

            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val datasetuDir = File(downloadDir, "DataSetu").apply { mkdirs() }

            val manifest = TransferProtocol.receiveManifest(clientSocket!!)
            val totalFiles = manifest?.first ?: 0
            val totalGlobalBytes = manifest?.second ?: 0L
            var filesTransferred = 0
            var globalBytesTransferred = 0L
            val transferredFilesList = mutableListOf<String>()

            _transferState.update { it.copy(totalFiles = totalFiles, totalBytes = totalGlobalBytes) }

            while (clientSocket?.isConnected == true) {
                var currentFileName = ""
                var fileBytesReceived = 0L
                val success = TransferProtocol.receiveFile(clientSocket!!, datasetuDir) { bytesReceived, speed, fileName ->
                    currentFileName = fileName
                    val increment = bytesReceived - fileBytesReceived
                    fileBytesReceived = bytesReceived
                    globalBytesTransferred += increment
                    
                    _transferState.update { 
                        it.copy(
                            bytesTransferred = globalBytesTransferred,
                            currentFileName = fileName,
                            currentSpeedBytesPerSec = speed
                        )
                    }
                }
                if (!success) break
                
                filesTransferred++
                transferredFilesList.add(currentFileName)
                _transferState.update { it.copy(filesTransferred = filesTransferred, transferredFiles = transferredFilesList) }
            }
            
            _transferState.update { it.copy(status = TransferStatus.COMPLETED) }
        } catch (e: Exception) {
            _transferState.update { it.copy(status = TransferStatus.ERROR, error = e.message) }
        } finally {
            closeConnections()
        }
    }

    suspend fun connectAndSendFiles(ipAddress: String, files: List<FileItem>) = withContext(Dispatchers.IO) {
        try {
            _transferState.update { it.copy(status = TransferStatus.CONNECTING) }
            
            val rawSocket = Socket()
            var connected = false
            var attempts = 0
            while (!connected && attempts < 5) {
                try {
                    rawSocket.connect(InetSocketAddress(ipAddress, TransferProtocol.DEFAULT_PORT), 2000)
                    connected = true
                } catch (e: Exception) {
                    attempts++
                    delay(500)
                }
            }
            if (!connected) throw Exception("Failed to connect after retries")
            
            clientSocket = TransferSocket(rawSocket)
            
            val totalBytes = files.sumOf { it.sizeBytes }
            _transferState.update { 
                it.copy(
                    status = TransferStatus.TRANSFERRING,
                    totalFiles = files.size,
                    totalBytes = totalBytes
                ) 
            }

            TransferProtocol.sendManifest(clientSocket!!, files.size, totalBytes)

            var globalBytesSent = 0L
            var filesTransferred = 0
            val transferredFilesList = mutableListOf<String>()

            for (fileItem in files) {
                var fileBytesSent = 0L
                TransferProtocol.sendFile(context, clientSocket!!, fileItem) { bytesSent, speed ->
                    val increment = bytesSent - fileBytesSent
                    fileBytesSent = bytesSent
                    globalBytesSent += increment
                    
                    _transferState.update { 
                        it.copy(
                            bytesTransferred = globalBytesSent,
                            currentFileName = fileItem.name,
                            currentSpeedBytesPerSec = speed
                        )
                    }
                }
                filesTransferred++
                transferredFilesList.add(fileItem.name)
                _transferState.update { it.copy(filesTransferred = filesTransferred, transferredFiles = transferredFilesList) }
            }
            
            TransferProtocol.sendEndAll(clientSocket!!)
            
            _transferState.update { it.copy(status = TransferStatus.COMPLETED) }
        } catch (e: Exception) {
            _transferState.update { it.copy(status = TransferStatus.ERROR, error = e.message) }
        } finally {
            closeConnections()
        }
    }

    private fun closeConnections() {
        try { clientSocket?.close() } catch (e: Exception) {}
        try { serverSocket?.close() } catch (e: Exception) {}
        clientSocket = null
        serverSocket = null
    }
}

data class TransferState(
    val status: TransferStatus = TransferStatus.IDLE,
    val bytesTransferred: Long = 0,
    val totalBytes: Long = 0,
    val currentFileName: String = "",
    val currentSpeedBytesPerSec: Long = 0,
    val totalFiles: Int = 0,
    val filesTransferred: Int = 0,
    val error: String? = null,
    val transferredFiles: List<String> = emptyList()
)

enum class TransferStatus {
    IDLE, CONNECTING, TRANSFERRING, COMPLETED, ERROR
}