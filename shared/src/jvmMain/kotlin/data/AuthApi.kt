package data

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import data.model.UserProfile

actual interface AuthApi {
    actual val auth: FirebaseAuth
    actual suspend fun signUp(email: String,password: String): Result<FirebaseUser?>
    actual suspend fun signIn(email: String,password: String): Result<FirebaseUser>
    actual suspend fun signOut(): Result<Unit>
    actual suspend fun saveUserProfile(userProfile: UserProfile, connection: FirebaseConnection): Result<Unit>
    actual suspend fun fetchUserProfile(uid: String): Result<UserProfile?>
    actual suspend fun uploadAndSaveProfileImage(image: Any): Result<Exception?>
    actual suspend fun downloadProfileImage(imageURL: String): Result<Any?>
    actual suspend fun setUserOnline()
    actual suspend fun setUserOffline()
}