package com.sonisoft.datasetu.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {

    val ONBOARDING_COMPLETED =
        booleanPreferencesKey("onboarding_completed")

    val IS_LOGGED_IN =
        booleanPreferencesKey("is_logged_in")

    val USER_NAME =
        stringPreferencesKey("user_name")

    val PASSWORD =
        stringPreferencesKey("password")

    val DEVICE_NAME =
        stringPreferencesKey("device_name")
}