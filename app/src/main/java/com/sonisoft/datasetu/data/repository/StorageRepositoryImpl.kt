package com.sonisoft.datasetu.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.sonisoft.datasetu.domain.model.StorageCategory
import com.sonisoft.datasetu.domain.model.StorageCategoryInfo
import com.sonisoft.datasetu.domain.model.StorageOverview
import com.sonisoft.datasetu.domain.repository.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StorageRepository {

    override fun getStorageOverview(): Flow<StorageOverview> = flow {
        while (true) {
            val overview = calculateStorageOverview()
            emit(overview)
            delay(5000) // Poll every 5 seconds for updates (could also use ContentObserver for real-time)
        }
    }.flowOn(Dispatchers.IO)

    private fun calculateStorageOverview(): StorageOverview {
        val path = Environment.getExternalStorageDirectory().absolutePath
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalSpace = totalBlocks * blockSize
        val freeSpace = availableBlocks * blockSize
        val usedSpace = totalSpace - freeSpace

        val imagesInfo = getCategoryInfo(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            StorageCategory.IMAGES
        )
        val videosInfo = getCategoryInfo(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            StorageCategory.VIDEOS
        )
        val audioInfo = getCategoryInfo(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            StorageCategory.AUDIO
        )
        
        // Documents: MIME types related to docs
        val docsInfo = getDocumentsInfo()

        val categorizedSpace = imagesInfo.sizeBytes + videosInfo.sizeBytes + audioInfo.sizeBytes + docsInfo.sizeBytes
        
        // Calculate "Other" space (everything else taking up room)
        val otherSpace = maxOf(0L, usedSpace - categorizedSpace)
        val otherInfo = StorageCategoryInfo(StorageCategory.OTHER, otherSpace, 0)

        return StorageOverview(
            totalSpaceBytes = totalSpace,
            freeSpaceBytes = freeSpace,
            usedSpaceBytes = usedSpace,
            categories = listOf(imagesInfo, videosInfo, audioInfo, docsInfo, otherInfo)
        )
    }

    private fun getCategoryInfo(uri: Uri, category: StorageCategory): StorageCategoryInfo {
        var totalSize = 0L
        var count = 0

        val projection = arrayOf(MediaStore.MediaColumns.SIZE)
        
        try {
            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                while (cursor.moveToNext()) {
                    totalSize += cursor.getLong(sizeColumn)
                    count++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return StorageCategoryInfo(category, totalSize, count)
    }

    private fun getDocumentsInfo(): StorageCategoryInfo {
        var totalSize = 0L
        var count = 0

        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns.SIZE)
        
        // Filter by document mime types or MediaStore.Files.FileColumns.MEDIA_TYPE == MEDIA_TYPE_DOCUMENT
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
        val selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT.toString())

        try {
            context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                while (cursor.moveToNext()) {
                    totalSize += cursor.getLong(sizeColumn)
                    count++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return StorageCategoryInfo(StorageCategory.DOCUMENTS, totalSize, count)
    }
}