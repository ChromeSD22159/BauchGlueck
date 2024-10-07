package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.ShoppingListDao
import data.local.entitiy.ShoppingList
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.datetime.Clock

class ShoppingListRepository(
    db: LocalDatabase,
    var serverHost: String,
    var user: FirebaseUser? = Firebase.auth.currentUser,
    var deviceID: String
): BaseRepository() {
    private var localService: ShoppingListDao = LocalDataSource(db).shoppingList

    fun getShoppingLists(): Flow<List<ShoppingList>> {
        val user = user ?: return emptyFlow()
        return localService.getShoppingLists(user.uid)
    }

    suspend fun insertShoppingList(shoppingList: ShoppingList) {
        val user = user ?: return
        localService.insertShoppingList(shoppingList.copy(userId = user.uid, updatedAtOnDevice = Clock.System.now().toEpochMilliseconds()))
    }
}