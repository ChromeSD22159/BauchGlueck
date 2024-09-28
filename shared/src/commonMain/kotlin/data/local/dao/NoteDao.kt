package data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.local.entitiy.Node
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(node: Node)

    @Query("SELECT * FROM node WHERE userID = :userID AND date > :startDate AND date < :endDate")
    fun getAllByDateRange(userID: String, startDate: Long, endDate: Long): Flow<List<Node>>

    @Query("SELECT * FROM node WHERE userId = :userID")
    fun getAllNotes(userID: String): Flow<List<Node>>

    @Query("SELECT * FROM node WHERE nodeId = :nodeId AND userId = :userID")
    suspend fun getNoteById(nodeId: String, userID: String): Node
}