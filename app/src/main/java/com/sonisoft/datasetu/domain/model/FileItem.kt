package com.sonisoft.datasetu.domain.model

import android.net.Uri

data class FileItem(
    val id: Long,
    val name: String,
    val sizeBytes: Long,
    val uri: Uri,
    val mimeType: String,
    val dateModified: Long
)