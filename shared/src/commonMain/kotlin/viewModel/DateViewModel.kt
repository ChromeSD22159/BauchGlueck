package viewModel

import util.DateRepository
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.CStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class DateViewModel: ViewModel() {
    private val dateRepository: DateRepository = DateRepository()

    init {
        viewModelScope.launch {
            loadNextSevenDays()
            loadPreviousSevenWeeks()
        }
    }

    private val _nextSevenDays: CMutableStateFlow<List<LocalDate>> = MutableStateFlow<List<LocalDate>>(listOf()).cMutableStateFlow()
    val nextSevenDays: CStateFlow<List<LocalDate>> = _nextSevenDays.asStateFlow().cStateFlow()

    private val _previousSevenWeeks: CMutableStateFlow<List<Pair<LocalDate, LocalDate>>> = MutableStateFlow<List<Pair<LocalDate, LocalDate>>>(listOf()).cMutableStateFlow()
    val previousSevenWeeks: CStateFlow<List<Pair<LocalDate, LocalDate>>> = _previousSevenWeeks.asStateFlow().cStateFlow()

    private fun loadNextSevenDays() {
        val nextSevenDays = dateRepository.getNextSevenDays()
        _nextSevenDays.value = nextSevenDays
    }

    private fun loadPreviousSevenWeeks() {
        val previousSevenWeeks = dateRepository.getFirstAndLastDayOfSevenPreviousWeeks()
        _previousSevenWeeks.value = previousSevenWeeks
    }
}