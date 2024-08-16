package data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import data.local.dao.CountdownTimerDao
import data.local.entitiy.CountdownTimer

@Database(
    entities = [CountdownTimer::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase: RoomDatabase(), DB {
    abstract val timerDao: CountdownTimerDao

     override fun clearAllTables() {
         super.clearAllTables()
    }
}

internal const val dbFileName = "LocalDatabase.db"

// Class 'LocalDatabase_Impl' is not abstract and does not implement abstract base class member 'clearAllTables'.
interface DB {
    fun clearAllTables() {}
}