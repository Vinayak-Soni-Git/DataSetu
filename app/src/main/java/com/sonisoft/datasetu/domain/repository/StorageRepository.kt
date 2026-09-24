package com.sonisoft.datasetu.domain.repository

import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.domain.model.StorageCategory
import com.sonisoft.datasetu.domain.model.StorageOverview
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun getStorageOverview(): Flow<StorageOverview>
    fun getFilesInCategory(category: StorageCategory): Flow<List<FileItem>>
}