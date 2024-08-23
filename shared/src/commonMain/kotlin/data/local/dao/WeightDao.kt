package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.Medication
import data.local.entitiy.WaterIntake
import data.local.entitiy.Weight
import kotlinx.datetime.Clock

@Dao
interface WeightDao {
    // GET
    @Query("SELECT * FROM weight WHERE userId = :userId AND isDeleted = false")
    suspend fun getAll(userId: String): List<Weight>

    @Query("SELECT * FROM weight WHERE weightId = :timerId AND isDeleted = false")
    suspend fun getById(timerId: String): Weight?

    @Query("SELECT * FROM weight WHERE updatedAt > :updatedAt AND userId = :userId AND isDeleted = false")
    suspend fun getAllAfterTimeStamp(updatedAt: Long, userId: String): List<Weight>

    // POST
    @Upsert
    suspend fun insertOrUpdate(item: Weight)

    @Update
    suspend fun updateMany(items: List<Weight>)

    // Delete
    @Query("UPDATE weight SET isDeleted = true WHERE userId = :timerId")
    suspend fun softDeleteById(timerId: String)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun softDeleteMany(items: List<Weight>)

    @Query("DELETE FROM weight WHERE userId = :userId")
    suspend fun hardDeleteAllByUserId(userId: String)

    @Delete
    suspend fun hardDeleteOne(item: Weight)

}