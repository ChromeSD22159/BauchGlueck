package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import data.local.entitiy.CountdownTimer

@Dao
interface CountdownTimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(countdownTimer: CountdownTimer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimers(countdownTimers: List<CountdownTimer>)

    @Query("SELECT * FROM CountdownTimer")
    suspend fun getAllTimer(): List<CountdownTimer>

    @Query("SELECT * FROM CountdownTimer WHERE updatedAt > :updatedAt")
    suspend fun getEntriesSinceLastUpdate(updatedAt: Long): List<CountdownTimer>

    @Query("SELECT * FROM CountdownTimer WHERE timerId = :timerId")
    suspend fun getTimerByTimerId(timerId: String): CountdownTimer?

    @Update
    suspend fun updateTimer(countdownTimer: CountdownTimer)

    @Update
    suspend fun updateTimers(countdownTimers: List<CountdownTimer>)

    @Query("DELETE FROM CountdownTimer WHERE timerId = :timerId")
    suspend fun deleteTimerById(timerId: String)
}