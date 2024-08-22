package data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

fun getDatabase(context: Context): LocalDatabase {
    val dbFile = context.applicationContext.getDatabasePath(dbFileName)
    return Room.databaseBuilder<LocalDatabase>(context, dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

/*
  /*return Room.databaseBuilder<LocalDatabase>(
        context = context.applicationContext,
        name = context.applicationContext.getDatabasePath(dbFileName).absolutePath
    ).build()*/
 */