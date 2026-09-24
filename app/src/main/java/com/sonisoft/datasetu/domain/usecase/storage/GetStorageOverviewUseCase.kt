package com.sonisoft.datasetu.domain.usecase.storage

import com.sonisoft.datasetu.domain.model.StorageOverview
import com.sonisoft.datasetu.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStorageOverviewUseCase @Inject constructor(
    private val storageRepository: StorageRepository
) {
    operator fun invoke(): Flow<StorageOverview> {
        return storageRepository.getStorageOverview()
    }
}