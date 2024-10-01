package viewModel

import data.Repository
import data.repositories.FirebaseRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShowAllNotesViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val firebaseRepository: FirebaseRepository by inject()

   private val _allNotes = repository.noteRepository.getAllNotes()
   val allNotes = _allNotes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}