package viewModel

import kotlinx.coroutines.flow.MutableStateFlow

interface BaseUiState<T> {
    val isLoading: MutableStateFlow<Boolean>
    val isFinishedSyncing: MutableStateFlow<Boolean>
    val minimumDelay: MutableStateFlow<Boolean>
    val hasError: MutableStateFlow<Boolean>
    val items: MutableStateFlow<List<T>>
}