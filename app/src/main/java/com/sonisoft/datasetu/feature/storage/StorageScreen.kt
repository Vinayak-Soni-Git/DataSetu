package com.sonisoft.datasetu.feature.storage

import android.text.format.Formatter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonisoft.datasetu.domain.model.StorageCategory
import com.sonisoft.datasetu.domain.model.StorageCategoryInfo

@Composable
fun StorageScreen(
    onNavigateToCategory: (String) -> Unit,
    viewModel: StorageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 48.dp, bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Storage",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (uiState.isLoading && uiState.storageOverview == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (uiState.storageOverview != null) {
            val overview = uiState.storageOverview!!

            item {
                // Storage Usage Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Used Space",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = Formatter.formatFileSize(
                                        context,
                                        overview.usedSpaceBytes
                                    ),
                                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = "${Formatter.formatFileSize(context, overview.totalSpaceBytes)} Total",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SegmentedProgressBar(
                            categories = overview.categories,
                            totalSpace = overview.totalSpaceBytes,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(MaterialTheme.shapes.small)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sort categories by size descending
            val sortedCategories = overview.categories.sortedByDescending { it.sizeBytes }

            items(sortedCategories) { categoryInfo ->
                StorageCategoryListItem(
                    info = categoryInfo,
                    totalUsedSpace = overview.usedSpaceBytes,
                    onClick = { onNavigateToCategory(categoryInfo.category.name) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

fun getCategoryColor(category: StorageCategory): Color {
    return when (category) {
        StorageCategory.IMAGES -> Color(0xFF4CAF50)
        StorageCategory.VIDEOS -> Color(0xFF2196F3)
        StorageCategory.AUDIO -> Color(0xFFFF9800)
        StorageCategory.DOCUMENTS -> Color(0xFF9C27B0)
        StorageCategory.APPS -> Color(0xFF00BCD4)
        StorageCategory.APKS -> Color(0xFF009688)
        StorageCategory.ARCHIVES -> Color(0xFF795548)
        StorageCategory.TRASH -> Color(0xFFF44336)
        StorageCategory.OTHER -> Color(0xFF607D8B)
        StorageCategory.SYSTEM -> Color(0xFFE91E63)
    }
}

@Composable
fun SegmentedProgressBar(
    categories: List<StorageCategoryInfo>,
    totalSpace: Long,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)

    Canvas(modifier = modifier) {
        // Draw background track
        drawRect(
            color = trackColor,
            size = size
        )

        if (totalSpace == 0L) return@Canvas

        var currentStartX = 0f

        // Draw segments
        for (categoryInfo in categories) {
            val width = (categoryInfo.sizeBytes.toFloat() / totalSpace.toFloat()) * size.width
            if (width > 0) {
                drawRect(
                    color = getCategoryColor(categoryInfo.category),
                    topLeft = Offset(currentStartX, 0f),
                    size = Size(width, size.height)
                )
                currentStartX += width
            }
        }
    }
}

@Composable
fun StorageCategoryListItem(
    info: StorageCategoryInfo,
    totalUsedSpace: Long,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val (title, icon, color) = when (info.category) {
        StorageCategory.IMAGES -> Triple("Images", Icons.Rounded.Image, getCategoryColor(StorageCategory.IMAGES))
        StorageCategory.VIDEOS -> Triple("Videos", Icons.Rounded.VideoFile, getCategoryColor(StorageCategory.VIDEOS))
        StorageCategory.AUDIO -> Triple("Audio", Icons.Rounded.AudioFile, getCategoryColor(StorageCategory.AUDIO))
        StorageCategory.DOCUMENTS -> Triple("Documents", Icons.Rounded.Folder, getCategoryColor(StorageCategory.DOCUMENTS))
        StorageCategory.APPS -> Triple("Apps", Icons.Rounded.Android, getCategoryColor(StorageCategory.APPS))
        StorageCategory.APKS -> Triple("APKs", Icons.Rounded.Android, getCategoryColor(StorageCategory.APKS))
        StorageCategory.ARCHIVES -> Triple("Archives", Icons.Rounded.Archive, getCategoryColor(StorageCategory.ARCHIVES))
        StorageCategory.TRASH -> Triple("Recently Deleted", Icons.Rounded.Delete, getCategoryColor(StorageCategory.TRASH))
        StorageCategory.OTHER -> Triple("Other Files", Icons.Rounded.InsertDriveFile, getCategoryColor(StorageCategory.OTHER))
        StorageCategory.SYSTEM -> Triple("System & App Data", Icons.Rounded.Memory, getCategoryColor(StorageCategory.SYSTEM))
    }

    val progress = if (totalUsedSpace > 0) (info.sizeBytes.toFloat() / totalUsedSpace.toFloat()) else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // If it's a category we can't fetch lists for easily, maybe disable click
                if (info.category != StorageCategory.SYSTEM && info.category != StorageCategory.OTHER) {
                    onClick()
                }
            },
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = Formatter.formatFileSize(context, info.sizeBytes),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}