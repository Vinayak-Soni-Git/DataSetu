package com.sonisoft.datasetu.feature.transfer.components

import android.media.MediaPlayer
import android.text.format.Formatter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sonisoft.datasetu.R
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

    // Play completion sound effect
    LaunchedEffect(transferState.status) {
        if (transferState.status == TransferStatus.COMPLETED) {
            try {
                val mediaPlayer = MediaPlayer.create(context, R.raw.success)
                mediaPlayer?.run {
                    setOnCompletionListener { mp ->
                        try { mp.release() } catch (e: Exception) { e.printStackTrace() }
                    }
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Accurate Progress Calculation
    val progress = when (transferState.status) {
        TransferStatus.COMPLETED -> 1.0f
        else -> if (transferState.totalBytes > 0) {
            (transferState.bytesTransferred.toDouble() / transferState.totalBytes.toDouble()).toFloat().coerceIn(0f, 1f)
        } else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "animatedProgress"
    )

    // Ensure transferred data display is 100% accurate at completion
    val displayBytesTransferred = if (transferState.status == TransferStatus.COMPLETED) {
        transferState.totalBytes
    } else {
        transferState.bytesTransferred.coerceAtMost(transferState.totalBytes)
    }

    val displaySpeedText = if (transferState.status == TransferStatus.TRANSFERRING) {
        "${Formatter.formatFileSize(context, transferState.currentSpeedBytesPerSec)}/s"
    } else {
        "0 B/s"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // 1. TOP SPEED HEADER DISPLAY
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(2.dp),
            color = Color(0xFF141414),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFF333333)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Speed,
                        contentDescription = "Speed",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TRANSFER SPEED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = Color(0xFFA0A0A0)
                    )
                }

                Text(
                    text = displaySpeedText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. STATUS CHIP BADGE
        MonochromeStatusChip(
            status = transferState.status,
            isReceiver = isReceiver
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3. LINEAR PROGRESS BAR WITH PERCENTAGE TEXT INSIDE
        LinearProgressBarWithPercentage(
            progress = animatedProgress,
            isCompleted = transferState.status == TransferStatus.COMPLETED
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4. DATA & FILES STATS CARDS (Black, White, Grey with 2.dp Rounded Corners)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Transferred Data Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFF2A2A2A)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DATA TRANSFERRED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color(0xFF888888)
                    )
                    Text(
                        text = "${Formatter.formatFileSize(context, displayBytesTransferred)} / ${Formatter.formatFileSize(context, transferState.totalBytes)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // File Count Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                shape = RoundedCornerShape(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414)),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFF2A2A2A)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FILES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = Color(0xFF888888)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Folder,
                            contentDescription = null,
                            tint = Color(0xFFA0A0A0),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "${transferState.filesTransferred} of ${transferState.totalFiles}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. TRANSFERRED FILES QUEUE CONTAINER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Transfer Queue",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color.White
            )
            Text(
                text = "${transferState.transferredFiles.size} Done",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = Color(0xFFA0A0A0)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(2.dp),
            color = Color(0xFF121212),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0xFF262626)))
        ) {
            LazyColumn(
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(transferState.transferredFiles) { fileName ->
                    MonochromeFileItem(fileName = fileName, isCompleted = true)
                }
                if (transferState.status == TransferStatus.TRANSFERRING && transferState.currentFileName.isNotEmpty()) {
                    item {
                        MonochromeFileItem(fileName = transferState.currentFileName, isCompleted = false)
                    }
                }
            }
        }

        // 6. ACTION BUTTON ON COMPLETION / ERROR
        if (transferState.status == TransferStatus.COMPLETED || transferState.status == TransferStatus.ERROR) {
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + scaleIn()
            ) {
                Button(
                    onClick = if (isReceiver) onReceiveMore else onSendMore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = if (isReceiver) "Receive More Files" else "Send More Files",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun MonochromeStatusChip(
    status: TransferStatus,
    isReceiver: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "monochrome_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val (statusText, icon) = when (status) {
        TransferStatus.CONNECTING -> Pair("CONNECTING...", Icons.Rounded.Sync)
        TransferStatus.TRANSFERRING -> Pair(
            if (isReceiver) "RECEIVING FILES..." else "SENDING FILES...",
            Icons.Rounded.Sync
        )
        TransferStatus.COMPLETED -> Pair("TRANSFER COMPLETE", Icons.Rounded.CheckCircle)
        TransferStatus.ERROR -> Pair("TRANSFER FAILED", Icons.Rounded.ErrorOutline)
        else -> Pair("READY", Icons.Rounded.Sync)
    }

    Surface(
        shape = RoundedCornerShape(2.dp),
        color = Color(0xFF1E1E1E),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(Color.White.copy(alpha = pulseAlpha * 0.5f))
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )
        }
    }
}

@Composable
fun LinearProgressBarWithPercentage(
    progress: Float,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val percentage = (progress * 100).toInt()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF3B3B3B), RoundedCornerShape(2.dp))
    ) {
        // Filled Progress Bar
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(2.dp))
                .background(if (isCompleted) Color.White else Color(0xFFE0E0E0))
        )

        // Percentage Text Centered INSIDE the Linear Progress Bar
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                ),
                color = if (progress > 0.5f) Color.Black else Color.White
            )
        }
    }
}

@Composable
fun MonochromeFileItem(
    fileName: String,
    isCompleted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF181818), RoundedCornerShape(2.dp))
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(2.dp))
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF262626)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.InsertDriveFile,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = fileName,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace
            ),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (isCompleted) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = "Done",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = Color.White
            )
        }
    }
}
