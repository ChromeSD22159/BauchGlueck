package de.frederikkohler.bauchglueck.data.network

import data.AuthApi
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.android
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.storage
import data.network.FirebaseCollection
import model.UserProfile
import java.io.ByteArrayOutputStream
import model.countdownTimer.CountdownTimer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository: AuthApi {
    override val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val storage: FirebaseStorage = Firebase.storage

    override suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> = suspendCoroutine { continuation ->
        val userRef = db.android.collection(FirebaseCollection.Users.collectionName).document(userProfile.uid)
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

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> = suspendCoroutine { continuation ->
        auth.android.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                auth.currentUser?.let { user ->
                    continuation.resume(Result.success(user))
                } ?: run {
                    continuation.resumeWithException(Exception("User is null"))
                }
            }
    }

    override suspend fun signOut(): Result<Unit> = suspendCoroutine { continuation ->
        try {
            auth.android.signOut()
            continuation.resume(Result.success(Unit))
        } catch (e: Exception) {
            continuation.resume(Result.failure(e))
        }
    }

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser?> = suspendCoroutine {
        auth.android.createUserWithEmailAndPassword(email, password)
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
            val storageRef = storage.reference.android
            val imageRef = storageRef.child("profile_images/$imageName")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val imageData = baos.toByteArray()

            val uploadTask = storageRef.putBytes(imageData)
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Update user profile (you'll likely need a separate function for this)
                    // updateUserProfileImageURL(user.uid, uri.toString())
                    continuation.resume(Result.success(null))
                }.addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
            }.addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
        }
    }

    override suspend fun fetchUserProfile(uid: String): Result<UserProfile?> = suspendCoroutine { continuation ->
        val userRef = db.android.collection(FirebaseCollection.Users.collectionName).document(uid)


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

    override suspend fun uploadAndSaveProfileImage(image: Any): Result<Exception?> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadProfileImage(imageURL: String): Result<Any?> = suspendCoroutine { continuation ->
        val storageRef = storage.reference.child(imageURL)

        val oneMegabyte: Long = 1024 * 1024
        storageRef.android.getBytes(oneMegabyte)
            .addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                continuation.resume(Result.success(bitmap))
            }
            .addOnFailureListener { e ->
                continuation.resume(Result.failure(e))
            }
    }

    override suspend fun fetchTimers(): Result<List<CountdownTimer>> = suspendCoroutine { continuation ->
        val timersRef = db.android.collection(FirebaseCollection.Timers.collectionName).whereEqualTo("userId", auth.currentUser?.uid)
        timersRef.get()
            .addOnSuccessListener { document ->
                val timers = document.documents.mapNotNull { doc ->

                    CountdownTimer(
                        id = doc.id,
                        userId = doc.data?.get("userId") as? String ?: "",
                        name = doc.data?.get("name") as? String ?: "",
                        duration = (doc.data?.get("duration") as? Number)?.toInt() ?: 0,
                        startDate = doc.data?.get("startDate") as? FieldValue,
                        endDate = doc.data?.get("endDate") as? FieldValue,
                        timerState = doc.data?.get("timerState") as? String ?: "",
                        timerType = doc.data?.get("timerType") as? String ?: "",
                        remainingDuration = (doc.data?.get("remainingDuration") as? Number)?.toInt() ?: 0,
                        notificate = doc.data?.get("notificate") as? Boolean ?: true,
                        showActivity = doc.data?.get("showActivity") as? Boolean ?: true
                    )

                   // doc.toObject(CountdownTimer::class.java)
                }
                Log.d("FirebaseRepository.timers", "Fetched timers: ${timers.size}")
                continuation.resume(Result.success(timers)) // timers
            }
            .addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
    }
}
