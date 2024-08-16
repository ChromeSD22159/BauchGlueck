package data.local

import androidx.room.Room
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabase(): LocalDatabase {
    val dbFilePath = documentDirectory() + "/$dbFileName"
    return Room.databaseBuilder<LocalDatabase>(
        name = dbFilePath,
    ).build()
}

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