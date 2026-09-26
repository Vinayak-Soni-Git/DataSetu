package com.sonisoft.datasetu.data.remote.transfer

import android.content.Context
import com.sonisoft.datasetu.domain.model.FileItem
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

object TransferProtocol {
    const val DEFAULT_PORT = 8080
    
    // Commands
    const val CMD_MANIFEST = 0
    const val CMD_FILE_INFO = 1
    const val CMD_FILE_DATA = 2
    const val CMD_END_TRANSFER = 3
    const val CMD_ALL_FILES_SENT = 4

    fun sendManifest(socket: TransferSocket, totalFiles: Int, totalBytes: Long) {
        val dataOut = DataOutputStream(socket.outputStream)
        dataOut.writeInt(CMD_MANIFEST)
        dataOut.writeInt(totalFiles)
        dataOut.writeLong(totalBytes)
        dataOut.flush()
    }

    fun receiveManifest(socket: TransferSocket): Pair<Int, Long>? {
        val dataIn = DataInputStream(socket.inputStream)
        return try {
            val cmd = dataIn.readInt()
            if (cmd == CMD_MANIFEST) {
                val files = dataIn.readInt()
                val bytes = dataIn.readLong()
                Pair(files, bytes)
            } else null
        } catch (e: Exception) { null }
    }
    
    fun sendFile(context: Context, socket: TransferSocket, fileItem: FileItem, onProgress: (Long, Long) -> Unit) {
        val dataOut = DataOutputStream(socket.outputStream)
        
        // Send file info
        dataOut.writeInt(CMD_FILE_INFO)
        dataOut.writeUTF(fileItem.name)
        dataOut.writeLong(fileItem.sizeBytes)
        
        // Send file data
        dataOut.writeInt(CMD_FILE_DATA)
        val fileIn = context.contentResolver.openInputStream(fileItem.uri) ?: return
        val buffer = ByteArray(64 * 1024) // 64KB
        var bytesRead: Int
        var totalBytesSent = 0L
        var bytesSinceLast = 0L
        var lastTime = System.currentTimeMillis()
        
        while (fileIn.read(buffer).also { bytesRead = it } != -1) {
            dataOut.write(buffer, 0, bytesRead)
            totalBytesSent += bytesRead
            bytesSinceLast += bytesRead
            
            val now = System.currentTimeMillis()
            val timeDiff = now - lastTime
            if (timeDiff >= 150) { // Update ~6-7 times a sec
                val speed = (bytesSinceLast * 1000) / timeDiff
                onProgress(totalBytesSent, speed)
                lastTime = now
                bytesSinceLast = 0
            }
        }
        fileIn.close()
        
        // Final progress report for this file
        val now = System.currentTimeMillis()
        val timeDiff = now - lastTime
        val speed = if (timeDiff > 0) (bytesSinceLast * 1000) / timeDiff else 0L
        onProgress(fileItem.sizeBytes, speed)

        // End transfer
        dataOut.writeInt(CMD_END_TRANSFER)
        dataOut.flush()
    }

    fun sendEndAll(socket: TransferSocket) {
        val dataOut = DataOutputStream(socket.outputStream)
        dataOut.writeInt(CMD_ALL_FILES_SENT)
        dataOut.flush()
    }
    
    fun receiveFile(socket: TransferSocket, saveDir: File, onProgress: (Long, Long, String) -> Unit): Boolean {
        val dataIn = DataInputStream(socket.inputStream)
        try {
            val cmd = dataIn.readInt()
            if (cmd == CMD_ALL_FILES_SENT) return false
            
            if (cmd == CMD_FILE_INFO) {
                val fileName = dataIn.readUTF()
                val fileSize = dataIn.readLong()
                
                if (dataIn.readInt() == CMD_FILE_DATA) {
                    val fileOut = FileOutputStream(File(saveDir, fileName))
                    val buffer = ByteArray(64 * 1024)
                    var bytesRead: Int
                    var totalBytesReceived = 0L
                    var bytesSinceLast = 0L
                    var lastTime = System.currentTimeMillis()
                    
                    var bytesRemaining = fileSize
                    while (bytesRemaining > 0) {
                        val bytesToRead = minOf(buffer.size.toLong(), bytesRemaining).toInt()
                        bytesRead = dataIn.read(buffer, 0, bytesToRead)
                        if (bytesRead == -1) break
                        
                        fileOut.write(buffer, 0, bytesRead)
                        totalBytesReceived += bytesRead
                        bytesRemaining -= bytesRead
                        bytesSinceLast += bytesRead
                        
                        val now = System.currentTimeMillis()
                        val timeDiff = now - lastTime
                        if (timeDiff >= 150) {
                            val speed = (bytesSinceLast * 1000) / timeDiff
                            onProgress(totalBytesReceived, speed, fileName)
                            lastTime = now
                            bytesSinceLast = 0
                        }
                    }
                    fileOut.close()
                    
                    // Final progress report for this file
                    val now = System.currentTimeMillis()
                    val timeDiff = now - lastTime
                    val speed = if (timeDiff > 0) (bytesSinceLast * 1000) / timeDiff else 0L
                    onProgress(fileSize, speed, fileName)

                    if (dataIn.readInt() == CMD_END_TRANSFER) {
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return false
    }
}
