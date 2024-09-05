package viewModel

import data.Repository
import data.local.entitiy.MedicationWithIntakeDetails
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.lighthousegames.logging.logging

class MedicationViewModel(
    private val repository: Repository,
): ViewModel() {

    private val medicationRepository = repository.medicationRepository
    private var scope = viewModelScope
    private val todayStart: Long
    private val todayEnd: Long

    init {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now().toLocalDateTime(timeZone)
        todayStart = now.date.atStartOfDayIn(timeZone).toEpochMilliseconds()
        todayEnd = todayStart + 86_400_000
    }

    val medicationsWithIntakeDetailsForToday: Flow<List<MedicationWithIntakeDetailsForToday>> = medicationRepository.getMedicationsWithIntakeTimesForToday()
        .map { medications ->

            logging().info { "Medications: $medications" }

            medications.map { medicationWithIntake ->
                val intakeTimesWithTodayStatus = medicationWithIntake.intakeTimesWithStatus.map { intakeTimeWithStatus ->
                    val todayStatuses = intakeTimeWithStatus.intakeStatuses.filter { status ->
                        status.date in todayStart..todayEnd
                    }
                    intakeTimeWithStatus.copy(intakeStatuses = todayStatuses)
                }
                medicationWithIntake.copy(intakeTimesWithStatus = intakeTimesWithTodayStatus)
            }
        }

    fun insertMedicationWithIntakeDetails(medicationWithIntakeDetails: MedicationWithIntakeDetails) {
        scope.launch {
            medicationRepository.insertMedicationWithIntakeDetails(medicationWithIntakeDetails)

            syncDataWithRemote()
        }
    }

    fun getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId: String): Flow<MedicationWithIntakeDetailsForToday> {
        return medicationRepository.getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId)
    }

    private fun syncDataWithRemote() {
        viewModelScope.launch {
            repository.medicationRepository.syncDataWithRemote()
        }
    }
}

/*
[
{
    "medication": {
    "id": 0,
    "medicationId": "3ABD-2Q4V-NBEJ-LL2C",
    "userId": "ipEn4nWseaU3IHrMV9Wy4Nio4wF2",
    "name": "testvvv",
    "dosage": "400mg",
    "isDeleted": false,
    "updatedAtOnDevice": 1725537090483
},
    "intakeTimesWithStatus": [
    {
        "intakeTime": {
        "intakeTimeId": "QHWD-7251-Q9BN-OZJF",
        "intakeTime": "13:51",
        "medicationId": "3ABD-2Q4V-NBEJ-LL2C",
        "isDeleted": false,
        "updatedAtOnDevice": 1725537103476
    },
        "intakeStatuses": [
        ]
    }
    ]
},
{
    "medication": {
    "id": 0,
    "medicationId": "MT98-JUE9-PSF8-KDJI",
    "userId": "ipEn4nWseaU3IHrMV9Wy4Nio4wF2",
    "name": "hdhdh",
    "dosage": "hdhd",
    "isDeleted": false,
    "updatedAtOnDevice": 1725541639517
},
    "intakeTimesWithStatus": [
    {
        "intakeTime": {
        "intakeTimeId": "ZQJZ-AKHK-6M0G-TLZB",
        "intakeTime": "15:10",
        "medicationId": "MT98-JUE9-PSF8-KDJI",
        "isDeleted": false,
        "updatedAtOnDevice": 1725541856436
    },
        "intakeStatuses": [
        {
            "intakeStatusId": "ZHCU-0SLN-KJE1-B26Z",
            "intakeTimeId": "ZQJZ-AKHK-6M0G-TLZB",
            "date": 1725547243437,
            "isTaken": true,
            "isDeleted": false,
            "updatedAtOnDevice": 1725547243437
        }
        ]
    },
    {
        "intakeTime": {
        "intakeTimeId": "C1SE-PX46-B0YK-IUUT",
        "intakeTime": "15:11",
        "medicationId": "MT98-JUE9-PSF8-KDJI",
        "isDeleted": false,
        "updatedAtOnDevice": 1725541876688
    },
        "intakeStatuses": [
        {
            "intakeStatusId": "FG94-9CCV-IAXP-EOW7",
            "intakeTimeId": "C1SE-PX46-B0YK-IUUT",
            "date": 1725547182171,
            "isTaken": true,
            "isDeleted": false,
            "updatedAtOnDevice": 1725547182171
        }
        ]
    }
    ]
}
]
 */