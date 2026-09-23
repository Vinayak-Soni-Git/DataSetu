package com.sonisoft.datasetu.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, error = "") }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = "") }
    }

    fun onSignupClick() {
        viewModelScope.launch {
            _events.emit(LoginEvent.NavigateToSignup)
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val session = userPreferences.userSession.first()
            
            if (session.userName == _uiState.value.username && session.password == _uiState.value.password && session.userName.isNotEmpty()) {
                userPreferences.setLoggedIn(true)
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(LoginEvent.NavigateToHome)
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Invalid username or password") }
            }
        }
    }
}