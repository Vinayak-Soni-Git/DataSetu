package com.sonisoft.datasetu.domain.model
data class UserSession(
    val onboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userName: String = "",
    val password: String = "",
    val deviceName: String = ""
)