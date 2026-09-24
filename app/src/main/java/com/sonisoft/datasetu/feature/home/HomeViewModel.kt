package com.sonisoft.datasetu.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.core.datastore.UserPreferences
import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.domain.usecase.storage.GetRecentFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val getRecentFilesUseCase: GetRecentFilesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferences.userSession.collect { session ->
                _uiState.update { it.copy(userName = session.userName) }
            }
        }
        loadRecentFiles()
    }

    private fun loadRecentFiles() {
        viewModelScope.launch {
            getRecentFilesUseCase().collect { files ->
                _uiState.update { 
                    it.copy(
                        recentFiles = files,
                        isRecentFilesLoading = false
                    )
                }
            }
        }
    }

    fun toggleFileSelection(fileItem: FileItem) {
        _uiState.update { state ->
            val newSelection = state.selectedFiles.toMutableSet()
            if (newSelection.contains(fileItem)) {
                newSelection.remove(fileItem)
            } else {
                newSelection.add(fileItem)
            }
            state.copy(selectedFiles = newSelection)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedFiles = emptySet()) }
    }
}