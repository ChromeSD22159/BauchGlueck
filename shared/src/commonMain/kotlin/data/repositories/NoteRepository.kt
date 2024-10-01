package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.NoteDao
import data.local.entitiy.Node
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.lighthousegames.logging.logging

class NoteRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
): BaseRepository() {
    private var localService: NoteDao = LocalDataSource(db).nodes

    suspend fun insert(node: Node) {
        val user = user ?: return
        node.userID = user.uid
        logging().info { "Inserting node: $node" }
        localService.insertNote(node)
    }

    fun getAllByDateRange(startDate: Long, endDate: Long): Flow<List<Node>> {
        val user = user ?: return emptyFlow()
        return localService.getAllByDateRange(user.uid, startDate, endDate)
    }

    fun getAllNotes(): Flow<List<Node>> {
        val user = user ?: return emptyFlow()
        return localService.getAllNotes(user.uid)
    }

    suspend fun getNode(noteId: String): Node? {
        val user = user ?: return null
        return localService.getNoteById(noteId, user.uid)
    }
}