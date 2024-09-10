package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.entitiy.ChangeLog

@Dao
interface ChangeLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(changeLog: ChangeLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<ChangeLog>)

    @Query("SELECT * FROM ChangeLog")
    suspend fun getAll(): List<ChangeLog>
}