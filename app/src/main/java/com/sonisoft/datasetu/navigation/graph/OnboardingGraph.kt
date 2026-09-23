package com.sonisoft.datasetu.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sonisoft.datasetu.feature.onboarding.OnboardingRoute
import com.sonisoft.datasetu.navigation.AppNavigator
import com.sonisoft.datasetu.navigation.route.OnboardingRoute as NavOnboardingRoute

fun NavGraphBuilder.onboardingGraph(navigator: AppNavigator) {

    composable(
        route = NavOnboardingRoute.Onboarding.route
    ) {
        OnboardingRoute(
            onNavigateToSignup = { navigator.navigateToSignup() }
        )
    }
}