package com.sonisoft.datasetu.feature.transfer.components

import android.text.format.Formatter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sonisoft.datasetu.manager.TransferState
import com.sonisoft.datasetu.manager.TransferStatus

@Composable
fun ModernTransferUI(
    transferState: TransferState,
    isReceiver: Boolean,
    onReceiveMore: () -> Unit = {},
    onSendMore: () -> Unit = {}
) {
    val context = LocalContext.current
    
    val progress = if (transferState.totalBytes > 0) {
        (transferState.bytesTransferred.toDouble() / transferState.totalBytes.toDouble()).toFloat()
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "progress"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (transferState.status) {
                TransferStatus.CONNECTING -> "Connecting..."
                TransferStatus.TRANSFERRING -> if (isReceiver) "Receiving..." else "Sending..."
                TransferStatus.COMPLETED -> "Transfer Complete!"
                TransferStatus.ERROR -> "Transfer Failed"
                else -> ""
            },
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = if (transferState.status == TransferStatus.COMPLETED) Color(0xFF4CAF50) 
                   else if (transferState.status == TransferStatus.ERROR) MaterialTheme.colorScheme.error 
                   else MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Circular Progress Indicator
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 12.dp
            )
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (transferState.status == TransferStatus.TRANSFERRING) {
                    Text(
                        text = "${Formatter.formatFileSize(context, transferState.currentSpeedBytesPerSec)}/s",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${transferState.filesTransferred} of ${transferState.totalFiles} files",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${Formatter.formatFileSize(context, transferState.bytesTransferred)} / ${Formatter.formatFileSize(context, transferState.totalBytes)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transferState.transferredFiles) { fileName ->
                    TransferredFileItem(fileName = fileName, isCompleted = true)
                }
                if (transferState.status == TransferStatus.TRANSFERRING) {
                    item {
                        TransferredFileItem(fileName = transferState.currentFileName, isCompleted = false)
                    }
                }
            }
        }
        
        if (transferState.status == TransferStatus.COMPLETED || transferState.status == TransferStatus.ERROR) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = if (isReceiver) onReceiveMore else onSendMore,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (isReceiver) "Receive More Files" else "Send More Files",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun TransferredFileItem(fileName: String, isCompleted: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.InsertDriveFile,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = fileName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (isCompleted) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Done",
                tint = Color(0xFF4CAF50)
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}