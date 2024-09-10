package data.remote.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import util.generateId

@Serializable
data class ShoppingListItem(
    val shoppingListItemId: String = generateId(),
    val name: String = "",
    val unit: String = "",
    val note: String = "",
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = Clock.System.now().toEpochMilliseconds()
)