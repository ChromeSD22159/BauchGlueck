package data.local

import android.content.Context
import androidx.room.Room

fun getDatabase(context: Context): LocalDatabase {
    return Room.databaseBuilder<LocalDatabase>(
        context = context.applicationContext,
        name = context.applicationContext.getDatabasePath(dbFileName).absolutePath
    ).build()
}