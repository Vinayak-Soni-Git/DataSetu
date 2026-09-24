package com.sonisoft.datasetu.domain.usecase.storage

import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentFilesUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(limit: Int = 20): Flow<List<FileItem>> {
        return storageRepository.getRecentFiles(limit)
    }
}