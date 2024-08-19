package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.entitiy.SyncHistory

@Dao
interface SyncHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncHistory(syncHistory: SyncHistory)

    @Query("SELECT * FROM SyncHistory WHERE deviceId = :deviceId ORDER BY lastSync DESC LIMIT 1")
    suspend fun getLatestSyncTimer(deviceId: String): SyncHistory?

    @Query("DELETE FROM SyncHistory")
    suspend fun deleteAllSyncHistory()
}