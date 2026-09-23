package com.sonisoft.datasetu.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(OnboardingUiState())

    val uiState: StateFlow<OnboardingUiState> =
        _uiState.asStateFlow()

    private val _events =
        MutableSharedFlow<OnboardingEvent>()

    val events: SharedFlow<OnboardingEvent> =
        _events.asSharedFlow()

    fun onGetStartedClicked() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            userPreferences.setOnboardingCompleted(
                completed = true
            )

            _uiState.update {
                it.copy(isLoading = false)
            }

            _events.emit(
                OnboardingEvent.NavigateToSignup
            )
        }
    }
}