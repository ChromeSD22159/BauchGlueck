package de.frederikkohler.bauchglueck.ui.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import de.frederikkohler.bauchglueck.manager.firebase.FirebaseManager
import de.frederikkohler.bauchglueck.manager.firebase.model.LoginNav
import de.frederikkohler.bauchglueck.manager.firebase.model.UserProfile
import java.io.ByteArrayOutputStream

class FirebaseAuthViewModel: ViewModel() {
    private val firebaseManager = FirebaseManager.shared

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    private val _userProfileImage = MutableLiveData<Bitmap?>()
    val userProfileImage: LiveData<Bitmap?> = _userProfileImage

    private val _nav = MutableLiveData<LoginNav>()
    val nav: LiveData<LoginNav> = _nav

    private val _showSyn = MutableLiveData<Boolean>()
    val showSyn: LiveData<Boolean> = _showSyn

    private val _email = MutableLiveData<String>()
    var email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    init {
        stateChangeListener()
    }

    private fun stateChangeListener() {
        _showSyn.value = true
        firebaseManager.auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            _user.value = currentUser
            if (currentUser != null) {
                fetchUserProfile(currentUser.uid) { userProfile ->
                    if (userProfile != null) {
                        _userProfile.value = userProfile
                        userProfile.profileImageURL?.let { url ->
                            downloadProfileImage(url) { result ->
                                result.onSuccess {
                                    _userProfileImage.value = it
                                }.onFailure {
                                    Log.e("FirebaseAuthManager", "Error downloading profile image", it)
                                }
                                _showSyn.value = false
                            }
                        }
                    } else {
                        Log.e("FirebaseAuthManager", "Failed to load user profile")
                    }
                }
                _nav.value = LoginNav.LOGGED
            } else {
                _nav.value = LoginNav.LOGIN
            }
        }
    }

    val initials: String
        get() {
            val firstName = _userProfile.value?.firstName.orEmpty()
            val lastName = _userProfile.value?.lastName.orEmpty()
            return if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                "${firstName.first().uppercaseChar()}${lastName.first().uppercaseChar()}"
            } else ""
        }

    fun saveUserProfile() {
        val userProfile = _userProfile.value ?: return
        val userRef = firebaseManager.db.collection("users").document(userProfile.uid)
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
            .addOnSuccessListener { Log.d("FirebaseAuthManager", "User profile saved successfully!") }
            .addOnFailureListener { e -> Log.e("FirebaseAuthManager", "Error saving user profile", e) }
    }

    fun signIn(email: String, password: String, complete: (Result<Unit>) -> Unit) {
        firebaseManager.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = task.result?.user
                    _nav.value = LoginNav.LOGGED
                    complete(Result.success(Unit))
                } else {
                    complete(Result.failure(task.exception ?: Exception("Unknown error")))
                }
            }
    }

    fun signOut() {
        try {
            firebaseManager.auth.signOut()
            _user.value = null
            _userProfile.value = null
            _userProfileImage.value = null
            _nav.value = LoginNav.LOGIN
            Log.d("FirebaseAuthManager", "Signed out")
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Sign out error", e)
        }
    }

    fun uploadAndSaveProfileImage(bitmap: Bitmap, completion: (Result<Unit>) -> Unit) {
        val user = _user.value ?: run {
            completion(Result.failure(Exception("User not logged in")))
            return
        }

        val imageName = "${user.uid}_profile_image.jpg"
        val storageRef = firebaseManager.storage.reference.child("profile_images/$imageName")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val imageData = baos.toByteArray()

        val uploadTask = storageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                _userProfile.value?.profileImageURL = uri.toString()
                saveUserProfile()
                _userProfileImage.value = bitmap
                completion(Result.success(Unit))
            }.addOnFailureListener { e ->
                completion(Result.failure(e))
            }
        }.addOnFailureListener { e ->
            completion(Result.failure(e))
        }
    }

    fun signUp(email: String, password: String, complete: (Result<FirebaseUser?>) -> Unit) {
        firebaseManager.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    complete(Result.success(task.result?.user))
                } else {
                    complete(Result.failure(task.exception ?: Exception("Unknown error")))
                }
            }
    }

    fun fetchUserProfile(uid: String, completion: (UserProfile?) -> Unit) {
        val userRef = firebaseManager.db.collection("users").document(uid)
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
                    completion(userProfile)
                } else {
                    Log.e("FirebaseAuthManager", "User profile not found")
                    completion(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAuthManager", "Error fetching user profile", e)
                completion(null)
            }
    }

    fun downloadProfileImage(imageURL: String, completion: (Result<Bitmap>) -> Unit) {
        val storageRef = firebaseManager.storage.reference.child(imageURL)

        val oneMegabyte: Long = 1024 * 1024
        storageRef.getBytes(oneMegabyte)
            .addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                completion(Result.success(bitmap))
            }
            .addOnFailureListener { e ->
                completion(Result.failure(e))
            }
    }
}