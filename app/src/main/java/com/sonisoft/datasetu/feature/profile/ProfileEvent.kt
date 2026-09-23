package com.sonisoft.datasetu.feature.profile

sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
}