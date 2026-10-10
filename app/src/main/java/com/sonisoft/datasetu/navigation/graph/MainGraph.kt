package com.sonisoft.datasetu.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sonisoft.datasetu.core.permission.PermissionRoute
import com.sonisoft.datasetu.feature.discovery.DiscoveryScreen
import com.sonisoft.datasetu.feature.history.HistoryScreen
import com.sonisoft.datasetu.feature.home.HomeRoute
import com.sonisoft.datasetu.feature.profile.ProfileRoute
import com.sonisoft.datasetu.feature.receive.ReceiveScreen
import com.sonisoft.datasetu.feature.send.FileSelectionScreen
import com.sonisoft.datasetu.feature.send.SendScreen
import com.sonisoft.datasetu.feature.settings.SettingsScreen
import com.sonisoft.datasetu.feature.storage.FilesListScreen
import com.sonisoft.datasetu.feature.storage.StorageScreen
import com.sonisoft.datasetu.feature.transfer.TransferScreen
import com.sonisoft.datasetu.feature.viewer.ImageViewerScreen
import com.sonisoft.datasetu.navigation.AppNavigator
import com.sonisoft.datasetu.navigation.route.MainRoute

fun NavGraphBuilder.mainGraph(navigator: AppNavigator) {
    composable(
        route = MainRoute.Home.route
    ) {
        HomeRoute(
            onNavigateToSend = { navigator.navigateToSend() },
            onNavigateToFileSelection = { navigator.navigateToFileSelection() },
            onNavigateToReceive = { navigator.navigateToReceive() },
            onNavigateToDiscovery = { navigator.navigateToDiscovery() },
            onNavigateToHistory = { navigator.navigateToHistory() },
            onNavigateToImageViewer = { uri -> 
                navigator.navController.navigate(MainRoute.ImageViewer.createRoute(uri))
            }
        )
    }

    composable(
        route = MainRoute.Storage.route
    ) {
        StorageScreen(
            onNavigateToCategory = { categoryName ->
                navigator.navController.navigate(MainRoute.FilesList.createRoute(categoryName))
            }
        )
    }

    composable(
        route = MainRoute.FilesList.route
    ) {
        FilesListScreen(
            onNavigateBack = { navigator.popBack() },
            onNavigateToImageViewer = { uri -> 
                navigator.navController.navigate(MainRoute.ImageViewer.createRoute(uri))
            }
        )
    }

    composable(
        route = MainRoute.ImageViewer.route,
        arguments = listOf(navArgument("uri") { 
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) { backStackEntry ->
        val uri = backStackEntry.arguments?.getString("uri") ?: ""
        ImageViewerScreen(
            uriString = uri,
            onNavigateBack = { navigator.popBack() }
        )
    }

    composable(
        route = MainRoute.Permission.route
    ) {
        PermissionRoute(
            onNavigateToHome = { navigator.navigateToHome() }
        )
    }

    composable(
        route = MainRoute.Discovery.route
    ) {
        DiscoveryScreen()
    }

    composable(
        route = MainRoute.FileSelection.route
    ) {
        FileSelectionScreen(
            onNavigateBack = { navigator.popBack() },
            onProceedToSend = { navigator.navigateToSend() }
        )
    }

    composable(
        route = MainRoute.Send.route
    ) {
        SendScreen(
            onNavigateBack = { navigator.popBack() },
            onNavigateToFileSelection = { navigator.navigateToFileSelection() }
        )
    }

    composable(
        route = MainRoute.Receive.route
    ) {
        ReceiveScreen()
    }

    composable(
        route = MainRoute.Transfer.route
    ) {
        TransferScreen()
    }

    composable(
        route = MainRoute.History.route
    ) {
        HistoryScreen()
    }

    composable(
        route = MainRoute.Settings.route
    ) {
        SettingsScreen()
    }

    composable(
        route = MainRoute.Profile.route
    ) {
        ProfileRoute(
            onNavigateBack = { navigator.popBack() }
        )
    }
}
