package de.frederikkohler.bauchglueck.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import model.FirebaseCollection
import model.LoginNav
import model.UserProfile
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> = suspendCoroutine { continuation ->
        val userRef = db.collection(FirebaseCollection.Users.collectionName).document(userProfile.uid)
        val userData = mapOf(
            "firstName" to userProfile.firstName,
            "lastName" to userProfile.lastName,
            "email" to userProfile.email,
            "surgeryDate" to userProfile.surgeryDate,
            "mainMeals" to userProfile.mainMeals,
            "betweenMeals" to userProfile.betweenMeals,
            "profileImageURL" to (userProfile.profileImageURL ?: ""),
            "startWeight" to userProfile.startWeight,
            "waterIntake" to userProfile.waterIntake,
            "waterDayIntake" to userProfile.waterDayIntake
        )

        userRef.set(userData)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { exception ->
                continuation.resume(Result.failure(exception))
            }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> = suspendCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Result.success(auth.currentUser!!))
                } else {
                    continuation.resume(Result.failure(task.exception ?: Exception("Sign-in failed")))
                }
            }
    }

    suspend fun signOut(): Result<Unit> = suspendCoroutine {
        try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> = suspendCoroutine {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Result.success(task.result?.user)
                } else {
                    Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    suspend fun uploadAndSaveProfileImage(
        bitmap: Bitmap
    ): Result<Exception?> = suspendCoroutine { continuation ->
        auth.currentUser?.let {  user ->
            val imageName = "${user.uid}_profile_image.jpg"
            val storageRef = storage.reference.child("profile_images/$imageName")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val imageData = baos.toByteArray()

            val uploadTask = storageRef.putBytes(imageData)
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update user profile (you'll likely need a separate function for this)
                    //updateUserProfileImageURL(user.uid, uri.toString())
                    continuation.resume(Result.success(null))
                }.addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
            }.addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
        }
    }

    suspend fun fetchUserProfile(uid: String): Result<UserProfile?> = suspendCoroutine { continuation ->
        val userRef = db.collection(FirebaseCollection.Users.collectionName).document(uid)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data ?: return@addOnSuccessListener
                    val userProfile = UserProfile(
                        uid = uid,
                        firstName = data["firstName"] as? String ?: "",
                        lastName = data["lastName"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        surgeryDate = data["surgeryDate"] as? Long ?: 0L,
                        mainMeals = data["mainMeals"] as? Int ?: 3,
                        betweenMeals = data["betweenMeals"] as? Int ?: 3,
                        profileImageURL = data["profileImageURL"] as? String,
                        startWeight = data["startWeight"] as? Double ?: 100.0,
                        waterIntake = data["waterIntake"] as? Double ?: 100.0,
                        waterDayIntake = data["waterDayIntake"] as? Double ?: 2000.0
                    )
                    continuation.resume(Result.success(userProfile))
                } else {
                    continuation.resume(Result.failure(Exception("User profile not found")))
                }
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }

    suspend fun downloadProfileImage(imageURL: String): Result<Bitmap?> = suspendCoroutine { continuation ->
        val storageRef = storage.reference.child(imageURL)

        val oneMegabyte: Long = 1024 * 1024
        storageRef.getBytes(oneMegabyte)
            .addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                continuation.resume(Result.success(bitmap))
            }
            .addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
    }
}
