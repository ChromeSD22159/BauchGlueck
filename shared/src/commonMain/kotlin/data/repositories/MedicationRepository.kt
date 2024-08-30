package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.MedicationDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.Medication
import data.remote.StrapiCountdownTimerApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock

class MedicationRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
) {

    private var localService: MedicationDao = LocalDataSource(db).medications
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory
    private val apiService: StrapiCountdownTimerApiClient = StrapiCountdownTimerApiClient(serverHost)

    suspend fun insertMedication(medication: Medication) {
        localService.insertMedication(medication)
    }

    suspend fun getAllMedications(): List<Medication> {
        return localService.getAllMedications(user!!.uid)
    }

    suspend fun updateMedication(medication: Medication) {
        localService.updateMedication(medication)
    }

    suspend fun softDeleteMedicationById(id: Int) {
        localService.softDeleteMedicationById(id)
    }
}