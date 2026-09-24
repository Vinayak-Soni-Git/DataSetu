package com.sonisoft.datasetu.feature.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.domain.usecase.storage.GetStorageOverviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val getStorageOverviewUseCase: GetStorageOverviewUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        loadStorageOverview()
    }

    private fun loadStorageOverview() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getStorageOverviewUseCase()
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load storage details"
                        )
                    }
                }
                .collect { overview ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            storageOverview = overview
                        )
                    }
                }
        }
    }
}