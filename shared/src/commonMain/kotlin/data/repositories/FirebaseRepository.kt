package data.repositories

import data.model.UserProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class FirebaseRepository() {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val collectionName :String = "UserProfile"

    val user get() = auth.currentUser

    suspend fun signIn(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password)
    }

    suspend fun signOut() {
        auth.signOut()
    }

    suspend fun createUserWithEmailAndPassword(userProfile: UserProfile, password: String): Error? {
        return try {
            val res = auth.createUserWithEmailAndPassword(userProfile.email, password)

            res.user?.uid?.let {
                userProfile.uid = it
                saveUserProfile(userProfile)
            }

            return null
        } catch (e: Exception) {
            Error("Error creating user: ${e.message}")
        }
    }

    suspend fun saveUserProfile(userProfile: UserProfile) {
        if (auth.currentUser == null) return
        firestore.collection(collectionName).document(auth.currentUser!!.uid).set(userProfile)
    }

    suspend fun readUserProfile(): UserProfile? {
        if (auth.currentUser == null) return null
        val documentSnapshot = firestore.collection(collectionName).document(auth.currentUser!!.uid).get()
        if (documentSnapshot.exists) {
            return documentSnapshot.data(UserProfile.serializer())
        }
        return null
    }
}