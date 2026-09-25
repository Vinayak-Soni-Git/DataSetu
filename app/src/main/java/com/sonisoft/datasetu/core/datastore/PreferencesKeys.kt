package com.sonisoft.datasetu.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {

    val ONBOARDING_COMPLETED =
        booleanPreferencesKey("onboarding_completed")

    val DEVICE_NAME =
        stringPreferencesKey("device_name")
}