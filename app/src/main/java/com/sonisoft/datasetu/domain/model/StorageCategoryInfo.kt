package com.sonisoft.datasetu.domain.model

data class StorageCategoryInfo(
    val category: StorageCategory,
    val sizeBytes: Long,
    val fileCount: Int
)