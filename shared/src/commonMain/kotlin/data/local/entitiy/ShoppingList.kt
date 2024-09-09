package data.local.entitiy

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "shoppingList",
    primaryKeys = ["shoppingListId"],
)
data class ShoppingList(
    val name: String,
    val shoppingListId: String,
    val userId: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val note: String,
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = 0
)

@Serializable
@Entity(
    tableName = "shoppingListItem",
    primaryKeys = ["shoppingListId"],
    foreignKeys = [
        ForeignKey(
            entity = ShoppingList::class,
            parentColumns = ["shoppingListId"],
            childColumns = ["shoppingListId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShoppingListItem(
    val shoppingListId: String,
    val shoppingListItemId: String,
    val name: String,
    val unit: String,
    val note: String,
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = 0
)

data class ShoppingListWithItems(
    @Embedded val shoppingList: ShoppingList,
    @Relation(
        parentColumn = "id",
        entityColumn = "shoppingListId"
    )
    val items: List<ShoppingListItem>
)
