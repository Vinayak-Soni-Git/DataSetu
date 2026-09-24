package com.sonisoft.datasetu.feature.receive

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.sonisoft.datasetu.manager.TransferStatus

@Composable
fun ReceiveScreen(
    viewModel: ReceiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val transferState by viewModel.transferState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (transferState.status == TransferStatus.IDLE || transferState.status == TransferStatus.CONNECTING) {
            Text(
                text = "Receive Files",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Scan this QR code from the Sender device to connect.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))

            if (uiState.qrContent.isNotEmpty()) {
                val bitmap = generateQrCode(uiState.qrContent)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(250.dp)
                    )
                }
            } else {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Waiting for connection...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            // Show transfer progress
            Text(
                text = "Transferring...",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "File: ${transferState.currentFileName}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            val progress = if (transferState.totalBytes > 0) {
                transferState.bytesTransferred.toFloat() / transferState.totalBytes.toFloat()
            } else 0f
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            if (transferState.status == TransferStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Transfer Complete!",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF4CAF50)
                )
            } else if (transferState.status == TransferStatus.ERROR) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Error: ${transferState.error}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun generateQrCode(content: String): Bitmap? {
    try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}