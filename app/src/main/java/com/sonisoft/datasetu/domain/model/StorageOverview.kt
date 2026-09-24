package com.sonisoft.datasetu.domain.model

data class StorageOverview(
    val totalSpaceBytes: Long,
    val freeSpaceBytes: Long,
    val usedSpaceBytes: Long,
    val categories: List<StorageCategoryInfo>
)