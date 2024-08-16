package de.frederikkohler.bauch_glueck.data.local.database

import android.content.Context
import androidx.room.Room
/*
actual object LocalDatabaseConstructor : RoomDatabaseConstructor<LocalDatabase> {
    lateinit var applicationContext: Context

    override fun initialize(): LocalDatabase {
        return Room.databaseBuilder<LocalDatabase>(
            context = applicationContext,
            name = applicationContext.getDatabasePath(dbFileName).absolutePath
        ).build()
    }

    actual fun setContext(context: Context) {
        applicationContext = context.applicationContext
    }
}
 */

fun getDatabase(context: Context): LocalDatabase {
    return Room.databaseBuilder<LocalDatabase>(
        context = context.applicationContext,
        name = context.applicationContext.getDatabasePath(dbFileName).absolutePath
    ).build()
}