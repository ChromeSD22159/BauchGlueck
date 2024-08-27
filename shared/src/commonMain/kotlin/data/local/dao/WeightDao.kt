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
import data.local.entitiy.Medication
import data.local.entitiy.WaterIntake
import data.local.entitiy.Weight
import kotlinx.datetime.Clock

@Dao
interface WeightDao {
    // GET
    @Query("SELECT * FROM weight WHERE userId = :userId AND isDeleted = false")
    suspend fun getAll(userId: String): List<Weight>

    @Query("SELECT * FROM weight WHERE weightId = :weightId AND isDeleted = false")
    suspend fun getById(weightId: String): Weight?

    @Query("SELECT * FROM weight WHERE updatedAtOnDevice > :updatedAtOnDevice AND userId = :userId")
    suspend fun getAllAfterTimeStamp(updatedAtOnDevice: Long, userId: String): List<Weight>

    // POST
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(weight: Weight): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(weight: Weight)

    @Transaction
    suspend fun insertOrUpdate(weight: Weight) {
        val id = insert(weight)
        if (id == -1L) {
            update(weight)
        }
    }

    // Delete
    @Update
    suspend fun updateMany(items: List<Weight>)

    // Delete
    @Query("UPDATE weight SET isDeleted = true AND updatedAtOnDevice = :updatedAtOnDevice WHERE weightId = :weightId")
    suspend fun softDeleteById(weightId: String, updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds())

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun softDeleteMany(items: List<Weight>)

    @Query("DELETE FROM weight WHERE userId = :userId")
    suspend fun hardDeleteAllByUserId(userId: String)

    @Delete
    suspend fun hardDeleteOne(item: Weight)

}