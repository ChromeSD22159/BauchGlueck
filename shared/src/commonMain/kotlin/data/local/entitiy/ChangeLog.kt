package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.remote.model.ChangeLogItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity
class ChangeLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val versionNumber: String = "",
    val releaseDate: String = "",
    var featuresString: String? = ""
) {
    var toChangeLogItem: List<ChangeLogItem>
        get() = try {
            featuresString?.let {
                Json.decodeFromString<List<ChangeLogItem>>(it)
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        set(value) {
            featuresString = Json.encodeToString(value)
        }
}



