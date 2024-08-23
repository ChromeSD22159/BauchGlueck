package data.local.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import data.local.RoomTable
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class SyncHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerialName("deviceId")
    var deviceId: String,

    @SerialName("table")
    @ColumnInfo(name = "table")
    var table: RoomTable,

    @SerialName("lastSync")
    var lastSync: Long = Clock.System.now().toEpochMilliseconds(),
) {
    val tableName: String
        get() = table.tableName
}