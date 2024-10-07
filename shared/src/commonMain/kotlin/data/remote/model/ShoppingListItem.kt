package data.remote.model

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import util.UUID
import viewModel.ShowAllNotesViewModel

@Serializable
data class ShoppingListItem(
    val shoppingListItemId: String = UUID.randomUUID(),
    val name: String = "",
    var amount: String = "",
    val unit: String = "",
    val note: String = "",
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = Clock.System.now().toEpochMilliseconds()
)