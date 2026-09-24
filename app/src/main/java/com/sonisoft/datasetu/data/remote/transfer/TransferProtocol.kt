package com.sonisoft.datasetu.data.remote.transfer

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object TransferProtocol {
    const val DEFAULT_PORT = 8080
    
    // Commands
    const val CMD_FILE_INFO = 1
    const val CMD_FILE_DATA = 2
    const val CMD_END_TRANSFER = 3
    
    fun sendFile(socket: TransferSocket, file: File, onProgress: (Long) -> Unit) {
        val dataOut = DataOutputStream(socket.outputStream)
        
        // Send file info
        dataOut.writeInt(CMD_FILE_INFO)
        dataOut.writeUTF(file.name)
        dataOut.writeLong(file.length())
        
        // Send file data
        dataOut.writeInt(CMD_FILE_DATA)
        val fileIn = FileInputStream(file)
        val buffer = ByteArray(8192)
        var bytesRead: Int
        var totalBytesSent = 0L
        
        while (fileIn.read(buffer).also { bytesRead = it } != -1) {
            dataOut.write(buffer, 0, bytesRead)
            totalBytesSent += bytesRead
            onProgress(totalBytesSent)
        }
        fileIn.close()
        
        // End transfer
        dataOut.writeInt(CMD_END_TRANSFER)
        dataOut.flush()
    }
    
    fun receiveFile(socket: TransferSocket, saveDir: File, onProgress: (Long, Long, String) -> Unit) {
        val dataIn = DataInputStream(socket.inputStream)
        
        if (dataIn.readInt() == CMD_FILE_INFO) {
            val fileName = dataIn.readUTF()
            val fileSize = dataIn.readLong()
            
            if (dataIn.readInt() == CMD_FILE_DATA) {
                val fileOut = FileOutputStream(File(saveDir, fileName))
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalBytesReceived = 0L
                
                // We need to read exactly fileSize bytes
                var bytesRemaining = fileSize
                while (bytesRemaining > 0) {
                    val bytesToRead = minOf(buffer.size.toLong(), bytesRemaining).toInt()
                    bytesRead = dataIn.read(buffer, 0, bytesToRead)
                    if (bytesRead == -1) break
                    
                    fileOut.write(buffer, 0, bytesRead)
                    totalBytesReceived += bytesRead
                    bytesRemaining -= bytesRead
                    onProgress(totalBytesReceived, fileSize, fileName)
                }
                fileOut.close()
                
                // Read end command
                if (dataIn.readInt() == CMD_END_TRANSFER) {
                    // Success
                }
            }
        }
    }
}