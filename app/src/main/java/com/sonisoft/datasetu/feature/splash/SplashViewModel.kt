package com.sonisoft.datasetu.feature.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _startupDestination = MutableSharedFlow<StartupDestination>()
    val startupDestination: SharedFlow<StartupDestination> = _startupDestination.asSharedFlow()

    fun determineDestination() {
        viewModelScope.launch {
            val userSession = userPreferences.userSession.first()
            val destination = when {
                !userSession.onboardingCompleted -> StartupDestination.Onboarding
                !userSession.isLoggedIn -> StartupDestination.Login
                else -> StartupDestination.Home
            }
            _startupDestination.emit(destination)
        }
    }
}