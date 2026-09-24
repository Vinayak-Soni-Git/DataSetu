package com.sonisoft.datasetu.feature.storage

import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.domain.model.StorageCategory

data class FilesListUiState(
    val category: StorageCategory? = null,
    val files: List<FileItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)