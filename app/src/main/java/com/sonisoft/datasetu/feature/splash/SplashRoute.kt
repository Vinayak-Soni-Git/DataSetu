package com.sonisoft.datasetu.feature.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SplashRoute(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.startupDestination.collect { destination ->
            when (destination) {
                is StartupDestination.Onboarding -> onNavigateToOnboarding()
                is StartupDestination.Login -> onNavigateToLogin()
                is StartupDestination.Home -> onNavigateToHome()
            }
        }
    }

    SplashScreen(
        onSplashFinished = {
            viewModel.determineDestination()
        }
    )
}