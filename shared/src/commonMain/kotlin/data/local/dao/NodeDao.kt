package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.entitiy.Node
import data.local.entitiy.ShoppingList
import data.local.entitiy.WaterIntake
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: Node)

    @Query("SELECT * FROM node WHERE userID = :userID AND date > :startDate AND date < :endDate")
    fun getAllByDateRange(userID: String, startDate: Long, endDate: Long): Flow<List<Node>>

    @Query("SELECT * FROM node WHERE userId = :userID")
    fun getAllNodes(userID: String): Flow<List<Node>>
}