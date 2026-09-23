package com.sonisoft.datasetu.navigation

import androidx.navigation.NavHostController
import com.sonisoft.datasetu.navigation.route.MainRoute
import com.sonisoft.datasetu.navigation.route.OnboardingRoute

class AppNavigator(
    private val navController: NavHostController
) {

    fun navigateToOnboarding() {
        navController.navigate(
            OnboardingRoute.Onboarding.route
        ) {
            popUpTo(
                OnboardingRoute.Splash.route
            ) {
                inclusive = true
            }

            launchSingleTop = true
        }
    }

    fun navigateToCreateProfile() {
        navController.navigate(
            OnboardingRoute.CreateProfile.route
        )
    }

    fun navigateToHome() {

        navController.navigate(
            MainRoute.Home.route
        ) {

            popUpTo(0) {
                inclusive = false
            }

            launchSingleTop = true
        }
    }

    fun navigateToDiscovery() {
        navController.navigate(
            MainRoute.Discovery.route
        )
    }

    fun navigateToSend() {
        navController.navigate(
            MainRoute.Send.route
        )
    }

    fun navigateToReceive() {
        navController.navigate(
            MainRoute.Receive.route
        )
    }

    fun navigateToTransfer() {
        navController.navigate(
            MainRoute.Transfer.route
        )
    }

    fun navigateToHistory() {
        navController.navigate(
            MainRoute.History.route
        )
    }

    fun navigateToSettings() {
        navController.navigate(
            MainRoute.Settings.route
        )
    }

    fun popBack() {
        navController.popBackStack()
    }
}