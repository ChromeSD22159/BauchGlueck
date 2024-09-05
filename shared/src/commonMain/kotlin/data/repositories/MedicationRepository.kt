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

class MedicationRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {
    private var localService: MedicationDao = LocalDataSource(db).medications
    private var syncManager: MedicationSyncManager = MedicationSyncManager(db, serverHost, deviceID)

    suspend fun insertMedicationWithIntakeDetails(medicationWithIntakeDetails: MedicationWithIntakeDetails) {
        localService.insertMedication(medicationWithIntakeDetails.medication)
        localService.insertIntakeTimes(medicationWithIntakeDetails.intakeTimesWithStatus.map { it.intakeTime })
        localService.insertIntakeStatuses(medicationWithIntakeDetails.intakeTimesWithStatus.flatMap { it.intakeStatuses })
    }

    fun getMedicationsWithIntakeTimesForToday(): Flow<List<MedicationWithIntakeDetailsForToday>> {
        return localService.getMedicationsWithIntakeTimesForToday()
    }

    fun getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId: String): Flow<MedicationWithIntakeDetailsForToday> {
        return localService.getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId)
    }

    suspend fun syncDataWithRemote() {
        syncManager.syncMedications()
    }
}