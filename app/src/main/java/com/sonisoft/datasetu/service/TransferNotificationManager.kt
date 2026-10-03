package com.sonisoft.datasetu.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sonisoft.datasetu.MainActivity
import com.sonisoft.datasetu.R

class TransferNotificationManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "File Transfers",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress for ongoing file transfers"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNotification(
        title: String,
        content: String,
        progress: Int,
        maxProgress: Int,
        indeterminate: Boolean = false
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Alternatively, use a transparent vector drawable like R.drawable.ic_transfer
            .setContentTitle(title)
            .setContentText(content)
            .setProgress(maxProgress, progress, indeterminate)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()
    }

    fun updateNotification(
        title: String,
        content: String,
        progress: Int,
        maxProgress: Int,
        indeterminate: Boolean = false
    ) {
        val notification = getNotification(title, content, progress, maxProgress, indeterminate)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "transfer_channel"
        const val NOTIFICATION_ID = 1001
    }
}
