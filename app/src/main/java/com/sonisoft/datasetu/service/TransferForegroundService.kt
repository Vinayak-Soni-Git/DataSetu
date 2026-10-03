package com.sonisoft.datasetu.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.ServiceCompat
import com.sonisoft.datasetu.manager.TransferManager
import com.sonisoft.datasetu.manager.TransferStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class TransferForegroundService : Service() {

    @Inject
    lateinit var transferManager: TransferManager

    private lateinit var notificationManager: TransferNotificationManager
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        notificationManager = TransferNotificationManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        val initialNotification = notificationManager.getNotification(
            title = "Transfer Starting",
            content = "Preparing to transfer files...",
            progress = 0,
            maxProgress = 100,
            indeterminate = true
        )

        try {
            ServiceCompat.startForeground(
                this,
                TransferNotificationManager.NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } catch (e: Exception) {
            Log.e("TransferService", "Failed to start foreground", e)
        }

        observeTransferState()

        return START_STICKY
    }

    private fun observeTransferState() {
        transferManager.transferState
            .onEach { state ->
                when (state.status) {
                    TransferStatus.CONNECTING -> {
                        notificationManager.updateNotification(
                            title = "Connecting",
                            content = "Establishing connection...",
                            progress = 0,
                            maxProgress = 100,
                            indeterminate = true
                        )
                    }
                    TransferStatus.TRANSFERRING -> {
                        val progress = if (state.totalBytes > 0) {
                            ((state.bytesTransferred.toFloat() / state.totalBytes.toFloat()) * 100).toInt()
                        } else {
                            0
                        }
                        
                        val speedMb = state.currentSpeedBytesPerSec / (1024f * 1024f)
                        val speedText = java.lang.String.format(java.util.Locale.US, "%.1f MB/s", speedMb)
                        
                        notificationManager.updateNotification(
                            title = "Transferring Files",
                            content = "${state.filesTransferred}/${state.totalFiles} files - $speedText",
                            progress = progress,
                            maxProgress = 100,
                            indeterminate = false
                        )
                    }
                    TransferStatus.COMPLETED, TransferStatus.ERROR, TransferStatus.IDLE -> {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                }
            }
            .launchIn(serviceScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_STOP = "ACTION_STOP_TRANSFER"
    }
}
