package com.sonisoft.datasetu.feature.storage

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.domain.model.StorageCategory
import com.sonisoft.datasetu.domain.usecase.storage.GetFilesInCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilesListViewModel @Inject constructor(
    private val getFilesInCategoryUseCase: GetFilesInCategoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilesListUiState())
    val uiState: StateFlow<FilesListUiState> = _uiState.asStateFlow()

    init {
        val categoryName = savedStateHandle.get<String>("categoryName") ?: ""
        try {
            val category = StorageCategory.valueOf(categoryName)
            loadFiles(category)
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = "Invalid category") }
        }
    }

    private fun loadFiles(category: StorageCategory) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, category = category) }
            getFilesInCategoryUseCase(category)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load files"
                        )
                    }
                }
                .collect { files ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            files = files
                        )
                    }
                }
        }
    }
}