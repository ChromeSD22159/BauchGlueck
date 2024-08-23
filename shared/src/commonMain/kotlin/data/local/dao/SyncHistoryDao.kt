package data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.RoomTable
import data.local.entitiy.SyncHistory
import kotlinx.datetime.Clock

@Dao
interface SyncHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncHistory(syncHistory: SyncHistory)

    @Query("SELECT * FROM SyncHistory WHERE deviceId = :deviceId ORDER BY lastSync")
    suspend fun getLatestSyncTimer(deviceId: String): List<SyncHistory>?



    @Query("DELETE FROM SyncHistory")
    suspend fun deleteAllSyncHistory()
}