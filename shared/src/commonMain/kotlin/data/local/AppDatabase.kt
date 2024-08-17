package data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import data.local.dao.CountdownTimerDao
import data.local.entitiy.CountdownTimer

@Database(
    entities = [CountdownTimer::class],
    version = 1,
    exportSchema = false,
    //autoMigrations = [MIGRATION_1_2]
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

// Entwicklung der Room DB
/*
In Production:
@Database(
    entities = [CountdownTimer::class],
    version = 1, // version++
    exportSchema = false,
    autoMigrations = [MIGRATION_1_2] // add Migrations
)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE deine_tabelle_name ADD COLUMN age INTEGER DEFAULT 0")
    }
}

IN DEV:
delete app from device and rebuild the project
oder: context.deleteDatabase("deine_datenbank_name")
 */