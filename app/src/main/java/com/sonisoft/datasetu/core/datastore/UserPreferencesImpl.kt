package com.sonisoft.datasetu.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.sonisoft.datasetu.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesImpl(
    private val context: Context
) : UserPreferences {

    override val userSession: Flow<UserSession> =
        context.dataStore.data.map { prefs ->

            UserSession(
                onboardingCompleted =
                    prefs[PreferenceKeys.ONBOARDING_COMPLETED]
                        ?: false,

                isLoggedIn =
                    prefs[PreferenceKeys.IS_LOGGED_IN]
                        ?: false,

                userName =
                    prefs[PreferenceKeys.USER_NAME]
                        ?: "",

                password =
                    prefs[PreferenceKeys.PASSWORD]
                        ?: "",

                deviceName =
                    prefs[PreferenceKeys.DEVICE_NAME]
                        ?: ""
            )
        }

    override suspend fun setOnboardingCompleted(
        completed: Boolean
    ) {

        context.dataStore.edit {

            it[PreferenceKeys.ONBOARDING_COMPLETED] =
                completed
        }
    }

    override suspend fun setLoggedIn(
        loggedIn: Boolean
    ) {

        context.dataStore.edit {

            it[PreferenceKeys.IS_LOGGED_IN] =
                loggedIn
        }
    }

    override suspend fun saveUserName(
        name: String
    ) {

        context.dataStore.edit {

            it[PreferenceKeys.USER_NAME] =
                name
        }
    }

    override suspend fun savePassword(
        password: String
    ) {

        context.dataStore.edit {

            it[PreferenceKeys.PASSWORD] =
                password
        }
    }

    override suspend fun saveDeviceName(
        deviceName: String
    ) {

        context.dataStore.edit {

            it[PreferenceKeys.DEVICE_NAME] =
                deviceName
        }
    }

    override suspend fun logout() {

        context.dataStore.edit {

            it[PreferenceKeys.IS_LOGGED_IN] =
                false
        }
    }
}