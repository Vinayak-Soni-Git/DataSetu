package com.sonisoft.datasetu.feature.onboarding

sealed interface OnboardingEvent {

    data object NavigateToSignup : OnboardingEvent
}