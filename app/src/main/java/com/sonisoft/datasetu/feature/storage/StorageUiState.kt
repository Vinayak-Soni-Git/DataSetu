package com.sonisoft.datasetu.feature.storage

import com.sonisoft.datasetu.domain.model.StorageOverview

data class StorageUiState(
    val isLoading: Boolean = true,
    val storageOverview: StorageOverview? = null,
    val error: String? = null
)