package com.sonisoft.datasetu.navigation.route

sealed class OnboardingRoute(
    override val route: String
) : NavigationDestination {

    data object Splash : OnboardingRoute("splash")

    data object Onboarding : OnboardingRoute("onboarding")
}