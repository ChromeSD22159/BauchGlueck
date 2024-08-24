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

@Dao
interface CountdownTimerDao {

    @Query("SELECT * FROM CountdownTimer WHERE userId = :userId AND isDeleted = false")
    suspend fun getAll(userId: String): List<CountdownTimer>

    @Query("SELECT * FROM CountdownTimer WHERE timerId = :timerId AND isDeleted = false")
    suspend fun getById(timerId: String): CountdownTimer?

    @Query("SELECT * FROM CountdownTimer WHERE updatedAt > :updatedAt AND userId = :userId")
    suspend fun getAllAfterTimeStamp(updatedAt: Long, userId: String): List<CountdownTimer>

    // POST
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(countdownTimer: CountdownTimer): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(countdownTimer: CountdownTimer)

    @Transaction
    suspend fun insertOrUpdate(countdownTimer: CountdownTimer) {
        val id = insert(countdownTimer)
        if (id == -1L) {
            update(countdownTimer)
        }
    }

    @Update
    suspend fun updateMany(items: List<CountdownTimer>)

    // Delete
    @Query("UPDATE CountdownTimer SET isDeleted = true WHERE timerId = :timerId")
    suspend fun softDeleteById(timerId: String)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun softDeleteMany(items: List<CountdownTimer>)

    @Query("DELETE FROM CountdownTimer WHERE userId = :userId")
    suspend fun hardDeleteAllByUserId(userId: String)

    @Delete
    suspend fun hardDeleteOne(item: CountdownTimer)
}
