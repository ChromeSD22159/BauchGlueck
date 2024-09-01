package viewModel

import data.Repository
import data.local.entitiy.Medication
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicationViewModel(
    private val repository: Repository,
): ViewModel(), BaseViewModel<MedicationUiState, Medication> {

    override val scope = viewModelScope

    private val _uiState: MutableStateFlow<MedicationUiState> = MutableStateFlow(MedicationUiState())
    override val uiState: StateFlow<MedicationUiState> = _uiState.asStateFlow()
    override fun updateItemAndSyncRemote(item: Medication) {
        TODO("Not yet implemented")
    }

    override fun addItem(item: Medication) {
        TODO("Not yet implemented")
    }

    override fun getAllItems() {
        TODO("Not yet implemented")
    }

    override fun syncDataWithRemote() {
        TODO("Not yet implemented")
    }

    override fun softDelete(item: Medication) {
        TODO("Not yet implemented")
    }

}

data class MedicationUiState(
    override val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val isFinishedSyncing: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val minimumDelay: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val hasError: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val items: MutableStateFlow<List<Medication>> = MutableStateFlow(emptyList()),
): BaseUiState<Medication>