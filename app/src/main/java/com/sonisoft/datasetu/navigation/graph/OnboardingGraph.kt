package com.sonisoft.datasetu.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sonisoft.datasetu.feature.onboarding.OnboardingScreen
import com.sonisoft.datasetu.feature.profile.ProfileScreen
import com.sonisoft.datasetu.feature.splash.SplashScreen
import com.sonisoft.datasetu.navigation.AppNavigator
import com.sonisoft.datasetu.navigation.route.OnboardingRoute

fun NavGraphBuilder.onboardingGraph(navigator: AppNavigator) {

    composable(
        route = OnboardingRoute.Splash.route
    ) {
        SplashScreen(onSplashFinished = {
            navigator.navigateToOnboarding()
        })
    }

    composable(
        route = OnboardingRoute.Onboarding.route
    ) {
        OnboardingScreen({})
    }

    composable(
        route = OnboardingRoute.CreateProfile.route
    ) {
        ProfileScreen()
    }
}