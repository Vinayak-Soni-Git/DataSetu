package com.sonisoft.datasetu.feature.auth.signup

sealed interface SignupEvent {
    data object NavigateToHome : SignupEvent
    data object NavigateToLogin : SignupEvent
}