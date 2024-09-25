package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import data.local.entitiy.Weight
import data.model.DailyAverage
import data.model.MonthlyAverage
import data.model.WeeklyAverage
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

@Dao
interface WeightDao {
    // GET
    @Query("SELECT * FROM weight WHERE userId = :userId AND isDeleted = false")
    suspend fun getAll(userId: String): List<Weight>

    @Query("SELECT * FROM weight WHERE userId = :userId AND isDeleted = false")
    fun getAllAsFlow(userId: String): Flow<List<Weight>>

    @Query("SELECT * FROM weight WHERE weightId = :weightId AND isDeleted = false")
    suspend fun getById(weightId: String): Weight?

    @Query("SELECT * FROM weight WHERE updatedAtOnDevice > :updatedAtOnDevice AND userId = :userId")
    suspend fun getAllAfterTimeStamp(updatedAtOnDevice: Long, userId: String): List<Weight>


    @Query("SELECT * FROM weight WHERE userId = :userId AND isDeleted = false ORDER BY weighed DESC LIMIT 1")
    fun getLastWeightFromUserId(userId: String): Flow<Weight?>

    @Query("""
        SELECT 
            AVG(value) AS avgValue, 
            weighed AS date
        FROM Weight
        WHERE isDeleted = 0
        AND weighed >= :startDate
        AND userId = :userId
        GROUP BY date 
        ORDER BY date DESC
        LIMIT :days
    """)
    suspend fun getAverageWeightLastDays(days: Int, startDate: Long, userId: String): List<DailyAverage>

    // POST
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(weight: Weight): Long

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