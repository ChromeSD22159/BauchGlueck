package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.CountdownTimer
import data.local.entitiy.IntakeTimes
import data.local.entitiy.Medication
import data.local.entitiy.MedicationWithIntakeTimes
import kotlinx.datetime.Clock

@Dao
interface MedicationDao {

    // Einfügen einer Medication
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication): Long

    // Einfügen mehrerer IntakeTimes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntakeTimes(intakeTimes: List<IntakeTimes>)

    @Query("SELECT * FROM medication WHERE updatedAt > :updatedAt AND userId = :userId AND isDeleted = false")
    suspend fun getAllMedicationAfterTimeStamp(updatedAt: Long, userId: String): List<Medication>

    // Aktualisieren einer Medication
    @Update
    suspend fun updateMedication(medication: Medication)

    // Aktualisieren einer IntakeTime
    @Update
    suspend fun updateIntakeTime(intakeTimes: IntakeTimes)

    // SoftLöschen einer Medication
    @Query("UPDATE medication SET isDeleted = true WHERE id = :id")
    suspend fun softDeleteMedicationById(id: Int)

    // SoftLöschen einer IntakeTime
    @Query("UPDATE IntakeTimes SET isDeleted = true WHERE id = :id")
    suspend fun softDeleteIntakeTimeById(id: Int)

    // Abrufen einer Medication mit zugehörigen IntakeTimes
    @Transaction
    @Query("SELECT * FROM medication WHERE id = :id")
    suspend fun getMedicationWithIntakeTimesById(id: Int): MedicationWithIntakeTimes?

    // Abrufen aller Medications mit ihren IntakeTimes
    @Transaction
    @Query("SELECT * FROM medication WHERE userId = :userId AND isDeleted = false")
    suspend fun getAllMedicationsWithIntakeTimes(userId: String): List<MedicationWithIntakeTimes>

    // Abrufen aller Medications (ohne IntakeTimes)
    @Query("SELECT * FROM medication Where userId = :userId AND isDeleted = false")
    suspend fun getAllMedications(userId: String): List<Medication>

    // Abrufen einer Medication anhand der medicationId
    @Query("SELECT * FROM medication WHERE medicationId = :medicationId AND isDeleted = false")
    suspend fun getMedicationByMedicationId(medicationId: String): Medication?
}