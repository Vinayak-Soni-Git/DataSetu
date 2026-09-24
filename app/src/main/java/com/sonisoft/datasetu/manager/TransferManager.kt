package com.sonisoft.datasetu.manager

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.sonisoft.datasetu.data.remote.transfer.TransferProtocol
import com.sonisoft.datasetu.data.remote.transfer.TransferSocket
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
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
    
    private val _transferState = MutableStateFlow(TransferState())
    val transferState: StateFlow<TransferState> = _transferState.asStateFlow()

    suspend fun startServerAndReceiveFiles() = withContext(Dispatchers.IO) {
        try {
            _transferState.update { it.copy(status = TransferStatus.CONNECTING) }
            serverSocket = ServerSocket(TransferProtocol.DEFAULT_PORT)
            
            // Wait for client connection
            val rawSocket = serverSocket!!.accept()
            clientSocket = TransferSocket(rawSocket)
            _transferState.update { it.copy(status = TransferStatus.TRANSFERRING) }

            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val datasetuDir = File(downloadDir, "DataSetu").apply { mkdirs() }

            // Loop to receive multiple files if needed, here we just show 1 for simplicity
            while (clientSocket?.isConnected == true) {
                TransferProtocol.receiveFile(clientSocket!!, datasetuDir) { bytesReceived, totalBytes, fileName ->
                    _transferState.update { 
                        it.copy(
                            bytesTransferred = bytesReceived,
                            totalBytes = totalBytes,
                            currentFileName = fileName
                        )
                    }
                }
                break // Just one for now
            }
            
            _transferState.update { it.copy(status = TransferStatus.COMPLETED) }
        } catch (e: Exception) {
            _transferState.update { it.copy(status = TransferStatus.ERROR, error = e.message) }
        } finally {
            closeConnections()
        }
    }

    suspend fun connectAndSendFiles(ipAddress: String, files: List<Uri>) = withContext(Dispatchers.IO) {
        try {
            _transferState.update { it.copy(status = TransferStatus.CONNECTING) }
            
            val rawSocket = Socket()
            rawSocket.connect(InetSocketAddress(ipAddress, TransferProtocol.DEFAULT_PORT), 5000)
            clientSocket = TransferSocket(rawSocket)
            
            _transferState.update { it.copy(status = TransferStatus.TRANSFERRING) }

            for (uri in files) {
                // We need to resolve URI to File, or write a URI stream sender. 
                // For simplicity, we use an absolute path logic here, or copy URI to a temp file.
                val file = getFileFromUri(uri)
                if (file != null && file.exists()) {
                    TransferProtocol.sendFile(clientSocket!!, file) { bytesSent ->
                        _transferState.update { 
                            it.copy(
                                bytesTransferred = bytesSent,
                                totalBytes = file.length(),
                                currentFileName = file.name
                            )
                        }
                    }
                }
            }
            
            _transferState.update { it.copy(status = TransferStatus.COMPLETED) }
        } catch (e: Exception) {
            _transferState.update { it.copy(status = TransferStatus.ERROR, error = e.message) }
        } finally {
            closeConnections()
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val dataIndex = it.getColumnIndex("_data")
                if (dataIndex != -1) {
                    return File(it.getString(dataIndex))
                }
            }
        }
        return null
    }

    private fun closeConnections() {
        clientSocket?.close()
        serverSocket?.close()
    }
}

data class TransferState(
    val status: TransferStatus = TransferStatus.IDLE,
    val bytesTransferred: Long = 0,
    val totalBytes: Long = 0,
    val currentFileName: String = "",
    val error: String? = null
)

enum class TransferStatus {
    IDLE, CONNECTING, TRANSFERRING, COMPLETED, ERROR
}