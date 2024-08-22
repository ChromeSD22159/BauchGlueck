package data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getDatabase(): RoomDatabase.Builder<LocalDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), dbFileName)
     Room.databaseBuilder(
        LocalDatabase::class.java,
        dbFile.absolutePath
    ).build()

    return TODO("Not yet implemented")
}