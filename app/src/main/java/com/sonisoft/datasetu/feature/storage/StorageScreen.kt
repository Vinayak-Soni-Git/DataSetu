package com.sonisoft.datasetu.feature.storage

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sonisoft.datasetu.domain.model.StorageCategory

@Composable
fun StorageScreen(
    viewModel: StorageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp)
    ) {
        Text(
            text = "Storage",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading && uiState.storageOverview == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.storageOverview != null) {
            val overview = uiState.storageOverview!!
            
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
                                text = Formatter.formatFileSize(context, overview.usedSpaceBytes),
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

                    LinearProgressIndicator(
                        progress = { 
                            if (overview.totalSpaceBytes > 0) 
                                (overview.usedSpaceBytes.toFloat() / overview.totalSpaceBytes.toFloat()) 
                            else 0f 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Map categories safely
            val images = overview.categories.find { it.category == StorageCategory.IMAGES }?.sizeBytes ?: 0L
            val videos = overview.categories.find { it.category == StorageCategory.VIDEOS }?.sizeBytes ?: 0L
            val audio = overview.categories.find { it.category == StorageCategory.AUDIO }?.sizeBytes ?: 0L
            val docs = overview.categories.find { it.category == StorageCategory.DOCUMENTS }?.sizeBytes ?: 0L
            val others = overview.categories.find { it.category == StorageCategory.OTHER }?.sizeBytes ?: 0L

            // Categories Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StorageCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = "Images",
                    size = Formatter.formatFileSize(context, images),
                    icon = Icons.Rounded.Image,
                    color = Color(0xFF4CAF50)
                )
                StorageCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = "Videos",
                    size = Formatter.formatFileSize(context, videos),
                    icon = Icons.Rounded.VideoFile,
                    color = Color(0xFF2196F3)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StorageCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = "Audio",
                    size = Formatter.formatFileSize(context, audio),
                    icon = Icons.Rounded.AudioFile,
                    color = Color(0xFFFF9800)
                )
                StorageCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = "Documents",
                    size = Formatter.formatFileSize(context, docs),
                    icon = Icons.Rounded.Folder,
                    color = Color(0xFF9C27B0)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StorageCategoryCard(
                    modifier = Modifier.weight(1f),
                    title = "Other Apps & System",
                    size = Formatter.formatFileSize(context, others),
                    icon = Icons.Rounded.InsertDriveFile,
                    color = Color(0xFF607D8B)
                )
            }
        }
    }
}

@Composable
fun StorageCategoryCard(
    modifier: Modifier = Modifier,
    title: String,
    size: String,
    icon: ImageVector,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = size,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}