package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import data.local.entitiy.CountdownTimer
import data.local.entitiy.WaterIntake
import data.local.entitiy.Weight
import kotlinx.datetime.Clock

@Dao
interface CountdownTimerDao {

    @Query("SELECT * FROM CountdownTimer WHERE userId = :userId AND isDeleted = false")
    suspend fun getAllCountdownTimers(userId: String): List<CountdownTimer>

    @Query("SELECT * FROM CountdownTimer WHERE timerId = :timerId AND isDeleted = false")
    suspend fun getCountdownTimerById(timerId: String): CountdownTimer?

    @Query("SELECT * FROM CountdownTimer WHERE updatedAt > :updatedAt AND userId = :userId AND isDeleted = false")
    suspend fun getCountdownTimersAfterTimeStamp(updatedAt: Long, userId: String): List<CountdownTimer>

    // POST
    @Upsert
    suspend fun insertOrUpdateCountdownTimer(countdownTimer: CountdownTimer)

    @Update
    suspend fun updateCountdownTimers(countdownTimers: List<CountdownTimer>, updatedAt: Long = Clock.System.now().toEpochMilliseconds())

    // Delete
    @Query("UPDATE CountdownTimer SET isDeleted = true WHERE userId = :userId")
    suspend fun softDeleteAllCountdownTimers(userId: String)

    @Query("UPDATE CountdownTimer SET isDeleted = true WHERE id IN (:ids)")
    suspend fun softDeleteCountdownTimer(countdownTimers: List<Int>)

    @Query("DELETE FROM CountdownTimer WHERE userId = :userId")
    suspend fun deleteAllCountdownTimers(userId: String)

    @Delete
    suspend fun deleteCountdownTimer(countdownTimer: CountdownTimer)
}