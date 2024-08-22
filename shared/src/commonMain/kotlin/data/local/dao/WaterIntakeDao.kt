package data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.WaterIntake
import kotlinx.datetime.Clock

@Dao
interface WaterIntakeDao {
    // GET
    @Query("SELECT * FROM water_intake WHERE userId = :userId AND isDeleted = false")
    suspend fun getAllWaterIntakes(userId: String): List<WaterIntake>

    @Query("SELECT * FROM water_intake WHERE id = :id AND isDeleted = false")
    suspend fun getWaterIntakesById(id: Int): WaterIntake?

    @Query("SELECT * FROM water_intake WHERE updatedAt > :updatedAt AND userId = :userId AND isDeleted = false")
    suspend fun getWaterIntakesAfterTimeStamp(updatedAt: Long, userId: String): List<WaterIntake>

    // POST
    @Upsert
    suspend fun insertOrUpdateWaterIntake(waterIntake: WaterIntake)

    @Update
    suspend fun updateWaterIntakes(waterIntakes: List<WaterIntake>, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    // Delete
    @Query("UPDATE water_intake SET isDeleted = true WHERE userId = :userId")
    suspend fun deleteAllWaterIntakes(userId: String)

    @Query("UPDATE water_intake SET isDeleted = true WHERE id IN (:ids)")
    suspend fun deleteWaterIntakes(waterIntakes: List<Int>)
}