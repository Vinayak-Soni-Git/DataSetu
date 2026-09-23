package com.sonisoft.datasetu.feature.splash

sealed interface StartupDestination {

    data object Onboarding : StartupDestination

    data object Login : StartupDestination

    data object Home : StartupDestination
}