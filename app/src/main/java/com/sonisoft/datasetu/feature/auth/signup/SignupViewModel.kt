package com.sonisoft.datasetu.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SignupEvent>()
    val events: SharedFlow<SignupEvent> = _events.asSharedFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = "") }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = "") }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, error = "") }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _events.emit(SignupEvent.NavigateToLogin)
        }
    }

    fun onSignupClick() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.username.isBlank() || state.password.isBlank()) {
                _uiState.update { it.copy(error = "Fields cannot be empty") }
                return@launch
            }
            if (state.password != state.confirmPassword) {
                _uiState.update { it.copy(error = "Passwords do not match") }
                return@launch
            }
            
            _uiState.update { it.copy(isLoading = true) }
            userPreferences.saveUserName(state.username)
            userPreferences.savePassword(state.password)
            userPreferences.setLoggedIn(true)
            
            _uiState.update { it.copy(isLoading = false) }
            _events.emit(SignupEvent.NavigateToHome)
        }
    }
}