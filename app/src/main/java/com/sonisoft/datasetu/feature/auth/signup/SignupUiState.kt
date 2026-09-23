package com.sonisoft.datasetu.feature.auth.signup

data class SignupUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String = ""
)