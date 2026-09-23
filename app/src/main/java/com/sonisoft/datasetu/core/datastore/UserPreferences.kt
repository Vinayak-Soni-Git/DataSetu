package com.sonisoft.datasetu.core.datastore

import com.sonisoft.datasetu.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface UserPreferences {

    val userSession: Flow<UserSession>

    suspend fun setOnboardingCompleted(
        completed: Boolean
    )

    suspend fun setLoggedIn(
        loggedIn: Boolean
    )

    suspend fun saveUserName(
        name: String
    )

    suspend fun savePassword(
        password: String
    )

    suspend fun saveDeviceName(
        deviceName: String
    )

    suspend fun logout()
}