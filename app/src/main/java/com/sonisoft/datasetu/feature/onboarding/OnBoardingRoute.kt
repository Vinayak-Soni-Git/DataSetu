package com.sonisoft.datasetu.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingRoute(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {

        viewModel.events.collect { event ->

            when (event) {

                OnboardingEvent.NavigateToHome -> {
                    onNavigateToHome()
                }
            }
        }
    }

    OnboardingScreen(
        onGetStarted = {
            viewModel.onGetStartedClicked()
        }
    )
}