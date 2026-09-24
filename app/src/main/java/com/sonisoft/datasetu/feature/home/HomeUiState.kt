package com.sonisoft.datasetu.feature.home

import com.sonisoft.datasetu.domain.model.FileItem

data class HomeUiState(
    val userName: String = "",
    val recentFiles: List<FileItem> = emptyList(),
    val isRecentFilesLoading: Boolean = true,
    val selectedFiles: Set<FileItem> = emptySet()
)