package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.CountdownTimer
import data.local.entitiy.Medication
import kotlinx.datetime.Clock

@Dao
interface MedicationDao {

    @Query("SELECT * FROM medication WHERE userId = :userId AND isDeleted = false")
    suspend fun getAll(userId: String): List<Medication>

    @Query("SELECT * FROM medication WHERE medicationId = :medicationId AND isDeleted = false")
    suspend fun getById(medicationId: String): Medication?

    @Query("SELECT * FROM medication WHERE updatedAt > :updatedAt AND userId = :userId AND isDeleted = false")
    suspend fun getAllAfterTimeStamp(updatedAt: Long, userId: String): List<Medication>

    // POST
    @Upsert
    suspend fun insertOrUpdate(item: Medication)

    @Update
    suspend fun updateMany(items: List<Medication>)

    // Delete
    @Query("UPDATE medication SET isDeleted = true WHERE medicationId = :medicationId")
    suspend fun softDeleteById(medicationId: String)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun softDeleteMany(items: List<Medication>)

    @Query("DELETE FROM medication WHERE userId = :userId")
    suspend fun hardDeleteAllByUserId(userId: String)

    @Delete
    suspend fun hardDeleteOne(item: Medication)
}