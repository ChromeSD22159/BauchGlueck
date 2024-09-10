package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.entitiy.ShoppingList

@Dao
interface ShoppingListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingList(shoppingList: ShoppingList): Long

    @Query("SELECT * FROM shoppingList WHERE shoppingListId = :shoppingListId AND isDeleted = 0")
    suspend fun getShoppingList(shoppingListId: String): ShoppingList?

    @Query("UPDATE shoppingList SET isDeleted = 1 WHERE shoppingListId = :shoppingListId")
    suspend fun softDeleteShoppingList(shoppingListId: String)

    @Query("SELECT * FROM shoppingList WHERE updatedAtOnDevice > :timestamp AND isDeleted = 0")
    suspend fun getShoppingListsUpdatedAfterTimeStamp(timestamp: Long): List<ShoppingList>
}