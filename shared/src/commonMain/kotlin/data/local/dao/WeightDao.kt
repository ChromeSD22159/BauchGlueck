package data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.Weight
import kotlinx.datetime.Clock

@Dao
interface WeightDao {
    // GET
    @Query("SELECT * FROM weight WHERE userId = :userId AND isDeleted = false")
    suspend fun getAllWeights(userId: String): List<Weight>

    @Query("SELECT * FROM weight WHERE id = :id AND isDeleted = false")
    suspend fun getWeightById(id: Int): Weight?

    @Query("SELECT * FROM weight WHERE updatedAt > :updatedAt AND userId = :userId AND isDeleted = false")
    suspend fun getWeightsAfterTimeStamp(updatedAt: Long, userId: String): List<Weight>

    // POST
    @Upsert
    suspend fun insertOrUpdateWeight(weight: Weight)

    @Update
    suspend fun updateWeights(weights: List<Weight>, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    // Delete
    @Query("UPDATE weight SET isDeleted = true WHERE userId = :userId")
    suspend fun deleteAllWeights(userId: String)

    @Query("UPDATE weight SET isDeleted = true WHERE id IN (:ids)")
    suspend fun deleteWeight(waterIntakes: List<Int>)
}