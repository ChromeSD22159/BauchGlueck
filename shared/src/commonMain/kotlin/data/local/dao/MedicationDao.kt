package data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.Medication
import kotlinx.datetime.Clock

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medication WHERE userId = :userId AND isDeleted = false")
    suspend fun getAllMedications(userId: String): List<Medication>

    @Query("SELECT * FROM medication WHERE id = :id AND isDeleted = false")
    suspend fun getMedicationById(id: Int): Medication?

    @Query("SELECT * FROM medication WHERE updatedAt > :updatedAt AND userId = :userId AND isDeleted = false")
    suspend fun getMedicationsAfterTimeStamp(updatedAt: Long, userId: String): List<Medication>

    // POST
    @Upsert
    suspend fun insertOrUpdateMedication(medication: Medication)

    @Update
    suspend fun updateMedications(medications: List<Medication>, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    // Delete
    @Query("UPDATE medication SET isDeleted = true WHERE userId = :userId")
    suspend fun deleteAllMedications(userId: String)

    @Query("UPDATE medication SET isDeleted = true WHERE id IN (:ids)")
    suspend fun deleteMedications(medications: List<Int>)
}