package com.sonisoft.datasetu.navigation

import android.os.Environment
import androidx.navigation.NavHostController
import com.sonisoft.datasetu.navigation.route.MainRoute
import com.sonisoft.datasetu.navigation.route.OnboardingRoute

class AppNavigator(
    val navController: NavHostController
) {

    fun navigateToOnboarding() {
        navController.navigate(OnboardingRoute.Onboarding.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToHome() {
        val destination = if (Environment.isExternalStorageManager()) {
            MainRoute.Home.route
        } else {
            MainRoute.Permission.route
        }

        navController.navigate(destination) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateBottomTab(route: String) {
        navController.navigate(route) {
            popUpTo(MainRoute.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToDiscovery() {
        navController.navigate(MainRoute.Discovery.route)
    }

    fun navigateToSend() {
        navController.navigate(MainRoute.Send.route)
    }

    fun navigateToFileSelection() {
        navController.navigate(MainRoute.FileSelection.route)
    }

    fun navigateToReceive() {
        navController.navigate(MainRoute.Receive.route)
    }

    fun navigateToTransfer() {
        navController.navigate(MainRoute.Transfer.route)
    }

    fun navigateToHistory() {
        navController.navigate(MainRoute.History.route)
    }

    fun navigateToSettings() {
        navController.navigate(MainRoute.Settings.route)
    }

    fun popBack() {
        navController.popBackStack()
    }
}