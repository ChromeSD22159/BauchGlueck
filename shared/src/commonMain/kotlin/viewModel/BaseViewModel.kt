package viewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface BaseViewModel<T, D> {
    val scope: CoroutineScope
    val uiState: StateFlow<T>
    fun addItem(item: D)
    fun updateItemAndSyncRemote(item: D)
    fun getAllItems()
    fun softDelete(item: D)
    fun syncDataWithRemote()
}