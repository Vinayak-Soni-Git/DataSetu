package com.sonisoft.datasetu.feature.auth.login

sealed interface LoginEvent {
    data object NavigateToHome : LoginEvent
    data object NavigateToSignup : LoginEvent
}