package com.sonisoft.datasetu.domain.usecase.storage

import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.domain.model.StorageCategory
import com.sonisoft.datasetu.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilesInCategoryUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(category: StorageCategory): Flow<List<FileItem>> {
        return storageRepository.getFilesInCategory(category)
    }
}