package com.sonisoft.datasetu.feature.send

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sonisoft.datasetu.domain.model.FileItem
import com.sonisoft.datasetu.domain.model.StorageCategory
import com.sonisoft.datasetu.domain.usecase.storage.GetFilesInCategoryUseCase
import com.sonisoft.datasetu.manager.TransferManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileSelectionViewModel @Inject constructor(
    private val getFilesInCategoryUseCase: GetFilesInCategoryUseCase,
    private val transferManager: TransferManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FileSelectionUiState())
    val uiState: StateFlow<FileSelectionUiState> = _uiState.asStateFlow()

    private val fileCache = mutableMapOf<StorageCategory, List<FileItem>>()

    init {
        loadCategory(StorageCategory.IMAGES) // Load default tab
    }

    fun loadCategory(category: StorageCategory) {
        if (fileCache.containsKey(category)) {
            _uiState.update { 
                it.copy(
                    currentCategory = category, 
                    currentFiles = fileCache[category]!!,
                    isLoading = false
                ) 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentCategory = category) }
            getFilesInCategoryUseCase(category)
                .catch { exception ->
                    _uiState.update { 
                        it.copy(isLoading = false, error = exception.message)
                    }
                }
                .collect { files ->
                    fileCache[category] = files
                    if (_uiState.value.currentCategory == category) {
                        _uiState.update { 
                            it.copy(isLoading = false, currentFiles = files)
                        }
                    }
                }
        }
    }

    fun toggleFileSelection(file: FileItem) {
        _uiState.update { state ->
            val newSet = state.selectedFiles.toMutableSet()
            if (newSet.contains(file)) {
                newSet.remove(file)
            } else {
                newSet.add(file)
            }
            state.copy(selectedFiles = newSet)
        }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedFiles = emptySet()) }
    }

    fun setFilesToSendAndProceed(onProceed: () -> Unit) {
        transferManager.filesToSend = _uiState.value.selectedFiles.toList()
        onProceed()
    }
}

data class FileSelectionUiState(
    val currentCategory: StorageCategory = StorageCategory.IMAGES,
    val currentFiles: List<FileItem> = emptyList(),
    val selectedFiles: Set<FileItem> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)