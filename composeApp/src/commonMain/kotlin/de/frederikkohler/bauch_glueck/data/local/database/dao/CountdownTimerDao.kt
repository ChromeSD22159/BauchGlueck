package de.frederikkohler.bauch_glueck.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer

@Dao
interface CountdownTimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(countdownTimer: CountdownTimer)

    @Query("SELECT * FROM CountdownTimer")
    suspend fun getAllTimer(): List<CountdownTimer>

    @Query("SELECT * FROM CountdownTimer WHERE lastUpdate > :lastUpdate AND userId = :userId")
    suspend fun getEntriesSinceLastUpdate(lastUpdate: Long, userId: String): List<CountdownTimer>

    @Query("SELECT * FROM CountdownTimer WHERE timerId = :timerId")
    suspend fun getTimerByTimerId(timerId: String): CountdownTimer?

    @Update
    suspend fun updateTimer(countdownTimer: CountdownTimer)

    @Query("DELETE FROM CountdownTimer WHERE timerId = :timerId")
    suspend fun deleteTimerById(timerId: String)
}