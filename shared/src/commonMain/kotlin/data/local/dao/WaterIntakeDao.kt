package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.Medication
import data.local.entitiy.WaterIntake
import kotlinx.datetime.Clock

@Dao
interface WaterIntakeDao {
    // GET
    @Query("SELECT * FROM water_intake WHERE userId = :userId AND isDeleted = false")
    suspend fun getAll(userId: String): List<WaterIntake>

    @Query("SELECT * FROM water_intake WHERE waterIntakeId = :waterIntakeId AND isDeleted = false")
    suspend fun getById(waterIntakeId: String): WaterIntake?

    @Query("SELECT * FROM water_intake WHERE updatedAtOnDevice > :updatedAtOnDevice AND userId = :userId AND isDeleted = false")
    suspend fun getAllAfterTimeStamp(updatedAtOnDevice: Long, userId: String): List<WaterIntake>

    // POST
    @Upsert
    suspend fun insertOrUpdate(item: WaterIntake)

    @Update
    suspend fun updateMany(items: List<WaterIntake>)

    // Delete
    @Query("UPDATE water_intake SET isDeleted = true WHERE waterIntakeId = :waterIntakeId")
    suspend fun softDeleteById(waterIntakeId: String)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun softDeleteMany(items: List<WaterIntake>)

    @Query("DELETE FROM water_intake WHERE userId = :userId")
    suspend fun hardDeleteAllByUserId(userId: String)

    @Delete
    suspend fun hardDeleteOne(item: WaterIntake)
}