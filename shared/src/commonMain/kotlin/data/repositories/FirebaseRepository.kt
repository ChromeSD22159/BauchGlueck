package data.repositories

import data.model.UserProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow

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

    suspend fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email)
    }

    suspend fun saveUserProfile(userProfile: UserProfile) {
        if (auth.currentUser == null) return
        firestore.collection(collectionName).document(auth.currentUser!!.uid).set(userProfile)
    }

    suspend fun readUserProfileById(userId: String): UserProfile? {
        val documentSnapshot = firestore.collection(collectionName).document(userId).get()
        return if (documentSnapshot.exists) {
            documentSnapshot.data(UserProfile.serializer())
        } else {
            null
        }
    }
}