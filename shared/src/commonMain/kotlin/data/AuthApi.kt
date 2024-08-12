package data

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import model.UserProfile
import model.countdownTimer.CountdownTimer

expect interface AuthApi {
    val auth: FirebaseAuth
    //val db: FirebaseFirestore
    //val storage: FirebaseStorage
    suspend fun signUp(email: String, password: String): Result<FirebaseUser?>
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signOut(): Result<Unit>
    suspend fun saveUserProfile(userProfile: UserProfile, connection: FirebaseConnection): Result<Unit>
    suspend fun fetchUserProfile(uid: String): Result<UserProfile?>
    suspend fun uploadAndSaveProfileImage(image: Any): Result<Exception?>
    suspend fun downloadProfileImage(imageURL: String): Result<Any?>
    suspend fun fetchTimers(): Result<List<CountdownTimer>>
    suspend fun setUserOnline()
    suspend fun setUserOffline()
}


enum class FirebaseConnection() {
    Local, Remote
}
