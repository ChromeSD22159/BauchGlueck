package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.model.Mood
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.UUID
import util.toIsoDate

@Serializable
@Entity
data class Node(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var text: String = "",
    var nodeId: String = UUID.randomUUID(),
    var userID: String = "",
    val date: Long = Clock.System.now().toEpochMilliseconds(),
    var moodsRawValue: String = "[]"
) {
    val dateString: String
        get() = date.toIsoDate()

    var moods: List<Mood>
        get() = try {
            Json.decodeFromString(moodsRawValue)
        } catch (e: Exception) {
            emptyList()
        }
        set(value) {
            moodsRawValue = Json.encodeToString(value)
        }
}