package data.local.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.remote.model.ShoppingListItem
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import util.UUID

@Serializable
@Entity(
    tableName = "shoppingList"
)
data class ShoppingList(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String = "",
    val shoppingListId: String = UUID.randomUUID(),
    val userId: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val note: String = "",
    val isComplete: Boolean = false,
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = Clock.System.now().toEpochMilliseconds(),
    var itemsString: String = ""
) {
    var items: List<ShoppingListItem>
        get() = try {
            Json.decodeFromString(itemsString)
        } catch (e: Exception) {
            emptyList()
        }
        set(value) {
            itemsString = Json.encodeToString(value)
        }
}


