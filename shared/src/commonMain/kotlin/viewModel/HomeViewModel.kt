package viewModel

import data.Repository
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import util.DateRepository

class HomeViewModel: ViewModel(), KoinComponent {
    private val repository: Repository by inject()
    private val today = DateRepository.startEndTodayCurrentTimeZone()

    private var _medicationsWithIntakeDetailsForToday = MutableStateFlow<List<MedicationWithIntakeDetailsForToday>>(emptyList())
    val medicationsWithIntakeDetailsForToday: StateFlow<List<MedicationWithIntakeDetailsForToday>> = _medicationsWithIntakeDetailsForToday.asStateFlow()

    private val _medicationListNotTakenToday = MutableStateFlow<List<MedicationWithIntakeDetailsForToday>>(emptyList())
    val medicationListNotTakenToday: StateFlow<List<MedicationWithIntakeDetailsForToday>> = _medicationListNotTakenToday.asStateFlow()

    init {
        loadMedicationsWithIntakeDetailsForToday()
        loadMedicationListNotTakenToday()
    }

    // find List of next medication to take orderd by time
    private fun loadMedicationListNotTakenToday() {
        viewModelScope.launch {
            val todayStart = today.start // Anfang des heutigen Tages
            val todayEnd = today.end // Ende des heutigen Tages

            // Lade alle Medikamente mit ihren Intake-Zeiten und Status
            val list = repository.medicationRepository.getMedicationsWithIntakeTimes()

            // Filtere und transformiere die Liste für heute
            val newList = list.mapNotNull { medicationWithIntakes ->
                // Filtere die IntakeTimes, um nur die zu behalten, die noch nicht genommen wurden oder keinen Status haben
                val intakeTimesForToday = medicationWithIntakes.intakeTimesWithStatus.mapNotNull { intakeTimeWithStatus ->
                    // Filtere die IntakeStatuses für den aktuellen IntakeTime nach folgenden Kriterien:
                    // 1. Kein Status vorhanden
                    // 2. Status vorhanden, aber isTaken == false
                    val statusesForToday = intakeTimeWithStatus.intakeStatuses.filter { status ->
                        status.date in todayStart..todayEnd && !status.isTaken
                    }

                    // Wenn es keine Status für heute gibt oder die gefilterten Status haben `isTaken == false`
                    if (statusesForToday.isNotEmpty() || intakeTimeWithStatus.intakeStatuses.isEmpty()) {
                        // Erstelle eine Kopie des IntakeTimeWithStatus mit den gefilterten Status
                        intakeTimeWithStatus.copy(intakeStatuses = statusesForToday)
                    } else {
                        null
                    }
                }

                // Wenn es keine IntakeTimes für heute gibt, ignoriere diesen Medikamenten-Eintrag
                if (intakeTimesForToday.isNotEmpty()) {
                    // Erstelle einen neuen MedicationWithIntakeDetailsForToday Eintrag
                    MedicationWithIntakeDetailsForToday(
                        medication = medicationWithIntakes.medication,
                        intakeTimesWithStatus = intakeTimesForToday
                    )
                } else {
                    null
                }
            }

            // Setze die neue gefilterte Liste
            _medicationListNotTakenToday.value = newList
        }
    }

    private fun loadMedicationsWithIntakeDetailsForToday() {
        viewModelScope.launch {
            val todayStart = today.start // Anfang des heutigen Tages
            val todayEnd = today.end // Ende des heutigen Tages

            // Lade alle Medikamente mit ihren Intake-Zeiten und Status
            val list = repository.medicationRepository.getMedicationsWithIntakeTimes()

            // Filtere und transformiere die Liste für heute
            val newList = list.mapNotNull { medicationWithIntakes ->
                // Filtere die IntakeTimes, um nur die zu behalten, die heute liegen
                val intakeTimesForToday = medicationWithIntakes.intakeTimesWithStatus.mapNotNull { intakeTimeWithStatus ->
                    // Filtere die IntakeStatuses für den aktuellen IntakeTime auf die heutigen Status
                    val statusesForToday = intakeTimeWithStatus.intakeStatuses.filter { status ->
                        status.date in todayStart..todayEnd
                    }
                    // Wenn es keine Status für heute gibt, ignoriere diesen Eintrag
                    if (statusesForToday.isNotEmpty()) {
                        // Erstelle eine Kopie des IntakeTimeWithStatus mit den gefilterten Status
                        intakeTimeWithStatus.copy(intakeStatuses = statusesForToday)
                    } else {
                        null
                    }
                }

                // Wenn es keine IntakeTimes für heute gibt, ignoriere diesen Medikamenten-Eintrag
                if (intakeTimesForToday.isNotEmpty()) {
                    // Erstelle einen neuen MedicationWithIntakeDetailsForToday Eintrag
                    MedicationWithIntakeDetailsForToday(
                        medication = medicationWithIntakes.medication,
                        intakeTimesWithStatus = intakeTimesForToday
                    )
                } else {
                    null
                }
            }

            // Setze die neue gefilterte Liste
            _medicationsWithIntakeDetailsForToday.value = newList
        }
    }
}