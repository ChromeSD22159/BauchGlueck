package data.repositories

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

open class BaseRepository() {
    val userId: String?
        get() = Firebase.auth.currentUser?.uid
}