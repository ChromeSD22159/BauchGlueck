package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.entitiy.SyncHistory

@Dao
interface SyncHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncHistory(syncHistory: SyncHistory)

    @Query("SELECT * FROM SyncHistory WHERE deviceId = :deviceId ORDER BY lastSync")
    suspend fun getLatestSyncTimer(deviceId: String): List<SyncHistory>

    @Query("DELETE FROM SyncHistory")
    suspend fun deleteAllSyncHistory()
}