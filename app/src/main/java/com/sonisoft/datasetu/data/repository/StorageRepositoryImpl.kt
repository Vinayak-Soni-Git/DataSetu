package com.sonisoft.datasetu.data.repository

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.domain.model.StorageCategory
import com.sonisoft.datasetu.domain.model.StorageCategoryInfo
import com.sonisoft.datasetu.domain.model.StorageOverview
import com.sonisoft.datasetu.domain.repository.StorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class StorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StorageRepository {

    override fun getStorageOverview(): Flow<StorageOverview> = flow {
        while (true) {
            val overview = calculateStorageOverview()
            emit(overview)
            delay(5000.milliseconds) // Poll every 5 seconds for updates (could also use ContentObserver for real-time)
        }
    }.flowOn(Dispatchers.IO)

    private fun calculateStorageOverview(): StorageOverview {
        val path = Environment.getDataDirectory().absolutePath
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val actualTotalSpace = totalBlocks * blockSize
        val freeSpace = availableBlocks * blockSize
        
        val totalSpace = getAdvertisedTotalSpace(actualTotalSpace)
        val usedSpace = totalSpace - freeSpace

        var imagesSize = 0L
        var videosSize = 0L
        var audioSize = 0L
        var docsSize = 0L
        var archivesSize = 0L
        var apksSize = 0L
        var otherSize = 0L

        var imagesCount = 0
        var videosCount = 0
        var audioCount = 0
        var docsCount = 0
        var archivesCount = 0
        var apksCount = 0
        var otherCount = 0

        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE
        )

        try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val typeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val size = cursor.getLong(sizeCol)
                    if (size <= 0) continue

                    val mediaType = cursor.getInt(typeCol)
                    val mimeType = cursor.getString(mimeCol) ?: ""

                    when {
                        mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> {
                            imagesSize += size
                            imagesCount++
                        }
                        mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> {
                            videosSize += size
                            videosCount++
                        }
                        mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO || mediaType == 3 /* PLAYLIST */ -> {
                            audioSize += size
                            audioCount++
                        }
                        mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT || mimeType.startsWith("text/") || mimeType.contains("pdf") || mimeType.contains("msword") || mimeType.contains("excel") -> {
                            docsSize += size
                            docsCount++
                        }
                        mimeType == "application/vnd.android.package-archive" -> {
                            apksSize += size
                            apksCount++
                        }
                        mimeType.contains("zip") || mimeType.contains("rar") || mimeType.contains("tar") || mimeType.contains("7z") -> {
                            archivesSize += size
                            archivesCount++
                        }
                        else -> {
                            otherSize += size
                            otherCount++
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        var trashSize = 0L
        var trashCount = 0
        try {
            val bundle = Bundle().apply {
                putInt(MediaStore.QUERY_ARG_MATCH_TRASHED, MediaStore.MATCH_ONLY)
            }
            context.contentResolver.query(uri, projection, bundle, null)?.use { cursor ->
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                while (cursor.moveToNext()) {
                    val size = cursor.getLong(sizeCol)
                    if (size > 0) {
                        trashSize += size
                        trashCount++
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Add installed apps size
        var installedAppsSize = 0L
        var installedAppsCount = 0
        try {
            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            for (app in packages) {
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    val file = File(app.sourceDir)
                    if (file.exists()) {
                        installedAppsSize += file.length()
                        installedAppsCount++
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val mediaTotal = imagesSize + videosSize + audioSize + docsSize + archivesSize + apksSize + otherSize + installedAppsSize + trashSize
        
        // System is the remaining used space (total used - all media we could find)
        val systemSpace = maxOf(0L, usedSpace - mediaTotal)

        return StorageOverview(
            totalSpaceBytes = totalSpace,
            freeSpaceBytes = freeSpace,
            usedSpaceBytes = usedSpace,
            categories = listOf(
                StorageCategoryInfo(StorageCategory.IMAGES, imagesSize, imagesCount),
                StorageCategoryInfo(StorageCategory.VIDEOS, videosSize, videosCount),
                StorageCategoryInfo(StorageCategory.AUDIO, audioSize, audioCount),
                StorageCategoryInfo(StorageCategory.DOCUMENTS, docsSize, docsCount),
                StorageCategoryInfo(StorageCategory.APPS, installedAppsSize, installedAppsCount),
                StorageCategoryInfo(StorageCategory.APKS, apksSize, apksCount),
                StorageCategoryInfo(StorageCategory.ARCHIVES, archivesSize, archivesCount),
                StorageCategoryInfo(StorageCategory.TRASH, trashSize, trashCount),
                StorageCategoryInfo(StorageCategory.OTHER, otherSize, otherCount),
                StorageCategoryInfo(StorageCategory.SYSTEM, systemSpace, 1)
            )
        )
    }

    private fun getAdvertisedTotalSpace(actualSpace: Long): Long {
        val gb = 1_000_000_000L
        val standardSizes = listOf(8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L)
        for (size in standardSizes) {
            if (actualSpace <= size * gb) {
                return size * gb
            }
        }
        return actualSpace
    }

    override fun getFilesInCategory(category: StorageCategory): Flow<List<FileItem>> = flow {
        val files = mutableListOf<FileItem>()
        
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )

        val bundle = Bundle().apply {
            putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC")
            if (category == StorageCategory.TRASH) {
                putInt(MediaStore.QUERY_ARG_MATCH_TRASHED, MediaStore.MATCH_ONLY)
            }
        }

        try {
            context.contentResolver.query(uri, projection, bundle, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val size = cursor.getLong(sizeCol)
                    if (size <= 0) continue

                    val mimeType = cursor.getString(mimeCol) ?: ""
                    
                    val isMatch = when (category) {
                        StorageCategory.IMAGES -> mimeType.startsWith("image/")
                        StorageCategory.VIDEOS -> mimeType.startsWith("video/")
                        StorageCategory.AUDIO -> mimeType.startsWith("audio/") || mimeType == "application/x-mpegurl"
                        StorageCategory.DOCUMENTS -> mimeType.startsWith("text/") || mimeType.contains("pdf") || mimeType.contains("msword") || mimeType.contains("excel")
                        StorageCategory.APKS -> mimeType == "application/vnd.android.package-archive"
                        StorageCategory.ARCHIVES -> mimeType.contains("zip") || mimeType.contains("rar") || mimeType.contains("tar") || mimeType.contains("7z")
                        StorageCategory.OTHER -> !mimeType.startsWith("image/") && !mimeType.startsWith("video/") && !mimeType.startsWith("audio/") && !mimeType.startsWith("text/") && !mimeType.contains("pdf") && !mimeType.contains("msword") && !mimeType.contains("excel") && mimeType != "application/vnd.android.package-archive" && !mimeType.contains("zip") && !mimeType.contains("rar") && !mimeType.contains("tar") && !mimeType.contains("7z")
                        StorageCategory.TRASH -> true // Since we already filtered MediaStore for only trashed items
                        else -> false
                    }

                    if (isMatch) {
                        val id = cursor.getLong(idCol)
                        val name = cursor.getString(nameCol) ?: "Unknown"
                        val date = cursor.getLong(dateCol) * 1000 // Convert to ms
                        
                        val fileUri = Uri.withAppendedPath(uri, id.toString())
                        
                        files.add(
                            FileItem(id, name, size, fileUri, mimeType, date)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        emit(files)
    }.flowOn(Dispatchers.IO)

    override fun getRecentFiles(limit: Int): Flow<List<FileItem>> = flow {
        val files = mutableListOf<FileItem>()
        
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val bundle = Bundle().apply {
            putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC")
            putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
            val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?, ?, ?)"
            val selectionArgs = arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT.toString()
            )
            putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
        }

        try {
            context.contentResolver.query(uri, projection, bundle, null)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)

                while (cursor.moveToNext()) {
                    val size = cursor.getLong(sizeCol)
                    if (size <= 0) continue

                    val mimeType = cursor.getString(mimeCol) ?: ""
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol) ?: "Unknown"
                    val date = cursor.getLong(dateCol) * 1000 // Convert to ms
                    
                    val fileUri = Uri.withAppendedPath(uri, id.toString())
                    
                    files.add(FileItem(id, name, size, fileUri, mimeType, date))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        emit(files)
    }.flowOn(Dispatchers.IO)
}