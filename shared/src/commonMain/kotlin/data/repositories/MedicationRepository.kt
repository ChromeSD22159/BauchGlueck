package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.MedicationDao
import data.local.entitiy.Medication
import data.local.entitiy.MedicationIntakeDataAfterTimeStamp
import data.local.entitiy.MedicationWithIntakeDetails
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import data.remote.syncManager.CountdownTimerSyncManager
import data.remote.syncManager.MedicationSyncManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.Clock

class MedicationRepository(
    db: LocalDatabase,
    var serverHost: String,
    var deviceID: String
): BaseRepository() {
    private var localService: MedicationDao = LocalDataSource(db).medications
    private var syncManager: MedicationSyncManager = MedicationSyncManager(db, serverHost, deviceID)

    suspend fun insertMedicationWithIntakeDetails(medicationWithIntakeDetails: MedicationWithIntakeDetails) {
        val times = medicationWithIntakeDetails.intakeTimesWithStatus.map { it.intakeTime }
        val statuses = medicationWithIntakeDetails.intakeTimesWithStatus.flatMap { it.intakeStatuses }
        localService.insertMedication(medicationWithIntakeDetails.medication.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))
        localService.insertIntakeTimes(times.map { it.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()) })
        localService.insertIntakeStatuses(statuses.map { it.copy(updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()) })
    }

    fun getMedicationsWithIntakeTimesForToday(): Flow<List<MedicationWithIntakeDetailsForToday>> {
        val currentUserId = userId ?: return emptyFlow()
        return localService.getMedicationsWithIntakeTimesForToday(currentUserId)
    }

    suspend fun getMedicationsWithIntakeTimes(): List<MedicationWithIntakeDetailsForToday> {
        val currentUserId = userId ?: return emptyList()
       return localService.getMedicationsWithIntakeTimes(currentUserId)
    }

    fun getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId: String): Flow<MedicationWithIntakeDetailsForToday> {
        val currentUserId = userId ?: return emptyFlow()
        return localService.getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId, currentUserId)
    }

    suspend fun syncDataWithRemote() {
        syncManager.syncMedications()
    }
}