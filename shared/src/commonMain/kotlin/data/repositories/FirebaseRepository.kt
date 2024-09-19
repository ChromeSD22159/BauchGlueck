package data.repositories

import com.mmk.kmpnotifier.notification.NotifierManager
import data.model.Firebase.FirebaseCloudMessagingResponse
import data.model.Firebase.RemoteNotification
import data.model.Firebase.ScheduleRemoteNotification
import data.model.Firebase.UserProfile
import data.remote.StrapiApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.messaging.messaging
import util.NetworkError
import util.Result

enum class Collection {
    UserProfile,
    UserNode
}

class FirebaseRepository() {
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth
    private val messaging = Firebase.messaging
    private val strapi = StrapiApiClient()

    val user get() = auth.currentUser

    suspend fun signIn(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password)
    }

    suspend fun signOut() {
        auth.signOut()
        NotifierManager.getPushNotifier().deleteMyToken()
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
        val userNotifierToken = NotifierManager.getPushNotifier().getToken()
        firestore.collection(Collection.UserProfile.name).document(auth.currentUser!!.uid).set(userProfile.copy(userNotifierToken = userNotifierToken ?: ""))
    }

    suspend fun readUserProfileById(userId: String): UserProfile? {
        val documentSnapshot = firestore.collection(Collection.UserProfile.name).document(userId).get()
        return if (documentSnapshot.exists) {
            documentSnapshot.data(UserProfile.serializer())
        } else {
            null
        }
    }

    suspend fun sendScheduleRemoteNotification(notification: ScheduleRemoteNotification): Result<FirebaseCloudMessagingResponse, NetworkError> {
        return strapi.sendScheduleRemoteNotification(notification)
    }

    suspend fun sendRemoteNotification(notification: RemoteNotification): Result<FirebaseCloudMessagingResponse, NetworkError> {
        return strapi.sendNotification(notification)
    }
}