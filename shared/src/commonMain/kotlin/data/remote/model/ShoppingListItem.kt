package data.remote.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import util.UUID

@Serializable
data class ShoppingListItem(
    val shoppingListItemId: String = UUID.randomUUID(),
    val name: String = "",
    val unit: String = "",
    val note: String = "",
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = Clock.System.now().toEpochMilliseconds()
)