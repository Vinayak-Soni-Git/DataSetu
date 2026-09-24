package com.sonisoft.datasetu.data.remote.transfer

import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class TransferSocket(private val socket: Socket) {
    val inputStream: InputStream = socket.getInputStream()
    val outputStream: OutputStream = socket.getOutputStream()
    val isConnected: Boolean get() = socket.isConnected && !socket.isClosed
    
    fun close() {
        try {
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}