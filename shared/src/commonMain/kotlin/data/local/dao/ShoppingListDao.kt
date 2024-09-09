package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import data.local.entitiy.ShoppingList
import data.local.entitiy.ShoppingListItem
import data.local.entitiy.ShoppingListWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(shoppingList: ShoppingList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingListItem(item: ShoppingListItem)

    // Lade eine Einkaufsliste mit allen Artikeln
    @Transaction
    @Query("SELECT * FROM shoppingList WHERE id = :shoppingListId AND isDeleted = 0")
    suspend fun getShoppingListWithItems(shoppingListId: Int): ShoppingListWithItems

    @Transaction
    @Query("SELECT * FROM shoppingListItem WHERE shoppingListId = :shoppingListId AND isDeleted = 0")
    suspend fun getShoppingListItems(shoppingListId: Int): Flow<List<ShoppingListWithItems>>

    @Query("UPDATE shoppingList SET isDeleted = 1 WHERE id = :shoppingListId")
    suspend fun softDeleteShoppingList(shoppingListId: Int)

    @Query("UPDATE shoppingListItem SET isDeleted = 1 WHERE id = :itemId")
    suspend fun softDeleteShoppingListItem(itemId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM shoppingList WHERE id = :shoppingListId AND isDeleted = 0)")
    suspend fun shoppingListExists(shoppingListId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM shoppingListItem WHERE shoppingListId = :shoppingListId AND isDeleted = 0)")
    suspend fun shoppingListHasItems(shoppingListId: Int): Boolean

    @Transaction
    @Query("""
        SELECT * FROM shoppingList 
        WHERE updatedAtOnDevice > :timestamp AND isDeleted = 0
    """)
    suspend fun getShoppingListsWithItemsUpdatedAfterTimeStamp(timestamp: Long): List<ShoppingListWithItems>

    @Transaction
    @Query("""
        SELECT * FROM shoppingListItem 
        WHERE updatedAtOnDevice > :timestamp AND isDeleted = 0
    """)
    suspend fun getShoppingListItemsUpdatedAfterTimeStamp(timestamp: Long): List<ShoppingListItem>

    @Transaction
    @Query("""
    SELECT * FROM shoppingList 
    WHERE updatedAtOnDevice > :lastSyncTimestamp AND isDeleted = 0
""")
    suspend fun getUpdatedShoppingListsAfterTimeStamp(lastSyncTimestamp: Long): List<ShoppingList>

    @Transaction
    @Query("""
    SELECT * FROM shoppingListItem 
    WHERE updatedAtOnDevice > :lastSyncTimestamp AND isDeleted = 0
""")
    suspend fun getUpdatedShoppingListItemsAfterTimeStamp(lastSyncTimestamp: Long): List<ShoppingListItem>

}