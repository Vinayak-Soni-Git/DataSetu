package com.sonisoft.datasetu.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomBarItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)