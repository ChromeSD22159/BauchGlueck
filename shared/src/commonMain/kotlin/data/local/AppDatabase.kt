package data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import data.local.dao.CountdownTimerDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.CountdownTimer
import data.local.entitiy.SyncHistory
import kotlinx.datetime.LocalDateTime

@Database(
    entities = [
        CountdownTimer::class,
        SyncHistory::class,
   ],
    version = 1,
    exportSchema = false
)
// @TypeConverters(LocalDateTimeConverter::class)
abstract class LocalDatabase: RoomDatabase(), DB {
    abstract val timerDao: CountdownTimerDao
    abstract val syncHistoryDao: SyncHistoryDao

     override fun clearAllTables() {
         super.clearAllTables()
    }
}

internal const val dbFileName = "LocalDatabase.db"


interface DB {
    fun clearAllTables() {}
}

class LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }
}