package de.frederikkohler.bauch_glueck.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabaseConstructor
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class LocalDatabaseConstructor : RoomDatabaseConstructor<LocalDatabase> {
    private val dbFilePath = documentDirectory() + "/$dbFileName"

    override fun initialize(): LocalDatabase {
        return Room.databaseBuilder<LocalDatabase>(
            name = dbFilePath,
        ).build()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}