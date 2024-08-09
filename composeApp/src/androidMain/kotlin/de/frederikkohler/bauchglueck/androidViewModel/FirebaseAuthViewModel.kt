package de.frederikkohler.bauchglueck.androidViewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import de.frederikkohler.bauchglueck.firebase.FirebaseRepository
import kotlinx.coroutines.launch
import model.LoginNav
import model.UserProfile

class FirebaseAuthViewModel(
    private val firebaseManager: FirebaseRepository = FirebaseRepository()
): ViewModel() {

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
        viewModelScope.launch {
            firebaseManager.auth.addAuthStateListener { firebaseAuth ->
                val currentUser = firebaseAuth.currentUser
                _user.value = currentUser
                if (currentUser != null) {
                    _nav.value = LoginNav.Logged
                    viewModelScope.launch {
                        val userProfileResult = firebaseManager.fetchUserProfile(currentUser.uid)
                        userProfileResult.onSuccess { userProfile ->
                            _userProfile.value = userProfile
                            userProfile?.profileImageURL?.let { profileImageURL ->
                                viewModelScope.launch {
                                    val imageResult = firebaseManager.downloadProfileImage(profileImageURL)
                                    imageResult.onSuccess { bitmap ->
                                        _userProfileImage.value = bitmap
                                    }.onFailure { exception ->
                                        // Handle image download error
                                        Log.e("ProfileViewModel", "Error downloading profile image", exception)
                                    }
                                }
                            }
                        }.onFailure { exception ->
                            Log.e("ProfileViewModel", "Error fetching user profile", exception)
                        }
                    }
                }
                else {
                    _nav.value = LoginNav.Login
                }
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

    private fun saveUserProfile() {
        viewModelScope.launch {
            _userProfile.value?.let { userProfile ->
                firebaseManager.saveUserProfile(userProfile).onSuccess {
                    Log.d("FirebaseAuthManager", "User profile saved successfully")
                }.onFailure {
                    Log.e("FirebaseAuthManager", "Error saving user profile", it)
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            firebaseManager.signIn(email, password).onSuccess {
                _user.value = it
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            firebaseManager.signOut().onSuccess {
                _user.value = null
                _userProfile.value = null
                _userProfileImage.value = null
                _nav.value = LoginNav.Login
            }
        }
    }

    fun signUp(email: String, password: String, complete: (Result<FirebaseUser?>) -> Unit) {
        viewModelScope.launch {
            val result = firebaseManager.signOut()
            result.onSuccess {
                _nav.value = LoginNav.Login
            }.onFailure {
                // Sign-out failed, handle the error (e.g., show an error message)
            }
        }
    }

    fun uploadAndSaveProfileImage(bitmap: Bitmap) {
        viewModelScope.launch {
            firebaseManager.uploadAndSaveProfileImage(bitmap).onSuccess {

            }.onFailure {

            }
        }
    }

    fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            firebaseManager.fetchUserProfile(uid).onSuccess {
                _userProfile.value = it
                it?.profileImageURL?.let { profileImageURL ->
                    firebaseManager.downloadProfileImage(profileImageURL)
                }
            }.onFailure {
                Log.e("FirebaseAuthManager", "Error fetching user profile", it)
            }
        }
    }

    fun navigateTo(view: LoginNav) {
        _nav.value = view
    }
}