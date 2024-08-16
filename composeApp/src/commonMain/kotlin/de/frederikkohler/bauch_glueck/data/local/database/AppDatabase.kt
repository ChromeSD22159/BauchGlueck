package de.frederikkohler.bauch_glueck.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.frederikkohler.bauch_glueck.data.local.database.dao.CountdownTimerDao
import de.frederikkohler.bauch_glueck.data.local.database.entitiy.CountdownTimer

@Database(
    entities = [CountdownTimer::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase: RoomDatabase(), DB  {
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