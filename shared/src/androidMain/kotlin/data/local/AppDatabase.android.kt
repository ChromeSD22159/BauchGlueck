package data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import de.frederikkohler.bauchglueck.shared.BuildKonfig
import kotlinx.coroutines.Dispatchers

fun getDatabase(context: Context): LocalDatabase {
    val isDev = BuildKonfig.DEV
    val dbFile = context.applicationContext.getDatabasePath(dbFileName)
    return Room.databaseBuilder<LocalDatabase>(context, dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(isDev)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}