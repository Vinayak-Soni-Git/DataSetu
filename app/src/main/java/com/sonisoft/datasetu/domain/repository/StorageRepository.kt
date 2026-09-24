package com.sonisoft.datasetu.domain.repository

import com.sonisoft.datasetu.domain.model.StorageOverview
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun getStorageOverview(): Flow<StorageOverview>
}