package data.repositories

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseRepository() {
    val user = Firebase.auth.currentUser
}