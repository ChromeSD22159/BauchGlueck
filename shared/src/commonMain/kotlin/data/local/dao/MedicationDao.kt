package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import data.local.entitiy.IntakeStatus
import data.local.entitiy.IntakeTime
import data.local.entitiy.Medication
import data.local.entitiy.MedicationIntakeData
import data.local.entitiy.MedicationIntakeDataAfterTimeStamp
import data.local.entitiy.MedicationWithIntakeDetailsForToday
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntakeTimes(intakeTimes: List<IntakeTime>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntakeStatuses(intakeStatuses: List<IntakeStatus>)

    @Query("SELECT * FROM Medication WHERE medicationId = :medicationId")
    suspend fun getMedicationByMedicationId(medicationId: String): Medication?

    @Query("SELECT * FROM IntakeTime WHERE intakeTimeId = :intakeTimeId")
    suspend fun getIntakeTimesByIntakeTimeId(intakeTimeId: String): IntakeTime?

    @Query("SELECT * FROM IntakeStatus WHERE intakeStatusId = :intakeStatusId")
    suspend fun getIntakeStatusesByIntakeStatusId(intakeStatusId: String): IntakeStatus?


    @Transaction
    @Query("""
        SELECT * FROM Medication
        WHERE EXISTS (
            SELECT * FROM IntakeTime
            WHERE Medication.medicationId = IntakeTime.medicationId
        )
        ORDER BY Medication.name ASC
    """)
    fun getMedicationsWithIntakeTimesForToday(): Flow<List<MedicationWithIntakeDetailsForToday>>

    @Transaction
    @Query("""
        SELECT * FROM Medication
        WHERE EXISTS (
            SELECT * FROM IntakeTime
            WHERE Medication.medicationId = IntakeTime.medicationId
        )
        ORDER BY Medication.name ASC
    """)
    suspend fun getMedicationsWithIntakeTimes(): List<MedicationWithIntakeDetailsForToday>

    @Transaction
    @Query("""
        SELECT * FROM Medication
        WHERE medicationId = :medicationId
        ORDER BY Medication.name ASC
    """)
    fun getMedicationsWithIntakeTimesForTodayByMedicationID(medicationId: String): Flow<MedicationWithIntakeDetailsForToday>

    @Transaction
    @Query("""
        SELECT * FROM Medication
        WHERE EXISTS (
            SELECT * FROM IntakeTime
            WHERE Medication.medicationId = IntakeTime.medicationId
            AND updatedAtOnDevice > :updatedAtOnDevice
            AND userId = :userId
        )
        AND updatedAtOnDevice > :updatedAtOnDevice
        AND userId = :userId
    """)
    suspend fun getMedicationsWithIntakeTimesAfterTimeStamp(updatedAtOnDevice: Long, userId: String): List<MedicationIntakeDataAfterTimeStamp>

    @Transaction
    @Query("""
        SELECT * FROM Medication
        WHERE EXISTS (
            SELECT * FROM IntakeTime
            WHERE Medication.medicationId = IntakeTime.medicationId
            AND userId = :userId
        )
        AND userId = :userId
    """)
    suspend fun getMedicationsWithIntakeTimesByMedicationID(userId: String): MedicationIntakeData?

}