package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class SyncHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerialName("deviceId")
    var deviceId: String = "",
    @SerialName("lastSync")
    var lastSync: Long = 0,
)