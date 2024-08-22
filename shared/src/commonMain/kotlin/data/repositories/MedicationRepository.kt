package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.MedicationDao
import data.local.entitiy.Medication
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock

class MedicationRepository(
    db: LocalDatabase,
    private var user: FirebaseUser? = Firebase.auth.currentUser
){
    private var medications: MedicationDao = LocalDataSource(db).medications

    suspend fun getAllMedications(): List<Medication> {
        return if (user == null) emptyList() else medications.getAllMedications(user!!.uid)
    }

    suspend fun getMedicationById(id: Int): Medication? = medications.getMedicationById(id)

    suspend fun insertOrUpdateMedication(medication: Medication) = medications.insertOrUpdateMedication(medication)

    suspend fun insertOrUpdateMedications(medications: List<Medication>) {
        medications.forEach {
            this.medications.insertOrUpdateMedication(it.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
        }
    }

    suspend fun updateMedications(medications: List<Medication>) {
        if (user == null) return
        this.medications.updateMedications(medications)
    }

    suspend fun deleteMedications(ids: List<Int>) {
        if (user == null) return
        medications.deleteMedications(ids)
    }

    suspend fun deleteAllMedications() {
        if (user == null) return
        medications.deleteAllMedications(user!!.uid)
    }

    suspend fun getMedicationsAfterTimeStamp(timeStamp: Long): List<Medication> {
        if (user == null) return emptyList()
        return medications.getMedicationsAfterTimeStamp(
            timeStamp,
            user!!.uid
        )
    }
}