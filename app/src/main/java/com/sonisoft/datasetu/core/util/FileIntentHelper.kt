package com.sonisoft.datasetu.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object FileIntentHelper {
    fun openFile(context: Context, uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Open file with"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}