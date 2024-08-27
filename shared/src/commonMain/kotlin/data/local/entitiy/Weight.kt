package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.DateConverter

@Serializable
@Entity
data class Weight(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerialName("userId")
    var userId: String = "",

    @SerialName("weightId")
    var weightId: String = "",

    @SerialName("value")
    var value: Double = 0.0,

    @SerialName("isDeleted")
    var isDeleted: Boolean = false,

    @SerialName("weighed")
    var weighed: String,

    @SerialName("updatedAtOnDevice")
    var updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds(),

    @SerialName("createdAt")
    @TypeConverters(DateConverter::class)
    var createdAt: String = Clock.System.now().toString(),

    @SerialName("updatedAt")
    @TypeConverters(DateConverter::class)
    var updatedAt: String = Clock.System.now().toString(),
)

