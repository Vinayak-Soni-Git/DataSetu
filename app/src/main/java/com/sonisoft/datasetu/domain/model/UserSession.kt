package com.sonisoft.datasetu.domain.model

data class UserSession(
    val onboardingCompleted: Boolean = false,
    val deviceName: String = ""
)