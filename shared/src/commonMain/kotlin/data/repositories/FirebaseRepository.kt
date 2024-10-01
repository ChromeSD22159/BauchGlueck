package data.repositories

import com.mmk.kmpnotifier.notification.NotifierManager
import data.model.firebase.UserProfile
import data.remote.BaseApiClient
import data.remote.StrapiApiClient
import data.remote.model.ApiDeviceToken
import data.remote.model.SyncResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.AuthResult
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.database
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import util.FirebaseCloudMessagingResponse
import util.NetworkError
import util.NotificationCronJobRequest
import util.Result

enum class Collection(var part: String) {
    UserProfile("UserProfile"),
    UserNode("UserNode"),
    OnlineUsers("bauchglueck-6c1cf/onlineUsers")
}

class FirebaseRepository() {
    private val firestore = Firebase.firestore
    private val database = Firebase.database
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

    suspend fun deleteDeviceToken(userID: String, token: String): Result<SyncResponse, NetworkError> {
        return strapi.sendData<ApiDeviceToken, SyncResponse>(
            BaseApiClient.ApiEndpoint.DeleteDeviceToken,
            ApiDeviceToken(
                userID,
                token
            )
        )
    }

    suspend fun saveDeviceToken(userID: String, token: String): Result<SyncResponse, NetworkError> {
        return strapi.sendData<ApiDeviceToken, SyncResponse>(
            BaseApiClient.ApiEndpoint.SaveDeviceToken,
            ApiDeviceToken(
                userID,
                token
            )
        )
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

    suspend fun getUserProfilesCount(): Int {
        val querySnapshot = firestore.collection(Collection.UserProfile.name).get()
        return querySnapshot.documents.size
    }

    suspend fun sendScheduleRemoteNotification(notification: NotificationCronJobRequest): Result<FirebaseCloudMessagingResponse, NetworkError> {
        return strapi.sendScheduleRemoteNotification(notification)
    }

    fun observeOnlineUserCount(onUserCountChange: (Int) -> Unit): Job {
        val onlineUsersReference = Firebase.database.reference(Collection.OnlineUsers.part)

        return CoroutineScope(Dispatchers.IO).launch {
            onlineUsersReference.valueEvents.collect { snapshot ->
                onUserCountChange(snapshot.children.count())
            }
        }
    }

    suspend fun getOnlineUserCount(onUserCount: (Int) -> Unit) {
        val onlineUsersReference = Firebase.database.reference(Collection.OnlineUsers.part)
        onlineUsersReference.valueEvents.collect {
            onUserCount(it.children.count())
        }
    }

    suspend fun markUserOnline() {
        val userId = auth.currentUser?.uid ?: return
        val userProfile = readUserProfileById(userId) ?: return
        val token = NotifierManager.getPushNotifier().getToken() ?: return
        val userReference = Firebase.database.reference("${Collection.OnlineUsers.part}/$userId")
        userReference.setValue(
            AppUser(userId, userProfile.firstName, token)
        )
        userReference.onDisconnect().removeValue()
    }

    suspend fun markUserOffline() {
        val userId = auth.currentUser?.uid ?: return
        val userReference = Firebase.database.reference("${Collection.OnlineUsers.part}/$userId")
        userReference.removeValue()
    }
}

@Serializable
data class AppUser(
    var name: String = "",
    var email: String = "",
    var appToken: String = "",
)