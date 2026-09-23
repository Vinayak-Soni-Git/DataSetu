package com.sonisoft.datasetu.core.di

import android.content.Context
import com.sonisoft.datasetu.core.datastore.UserPreferences
import com.sonisoft.datasetu.core.datastore.UserPreferencesImpl

object AppContainer {

    lateinit var userPreferences: UserPreferences

    fun init(
        context: Context
    ) {

        userPreferences =
            UserPreferencesImpl(context)
    }
}