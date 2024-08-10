package de.frederikkohler.bauchglueck.viewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import de.frederikkohler.bauchglueck.data.network.FirebaseRepository
import kotlinx.coroutines.launch
import navigation.PublicNav
import model.UserProfile
import model.countdownTimer.CountdownTimer

class FirebaseAuthViewModel(
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
): ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    private val _userProfileImage = MutableLiveData<Bitmap?>()
    val userProfileImage: LiveData<Bitmap?> = _userProfileImage

    private val _nav = MutableLiveData<PublicNav>()
    val nav: LiveData<PublicNav> = _nav

    private val _showSyn = MutableLiveData<Boolean>()
    val showSyn: LiveData<Boolean> = _showSyn

    private val _email = MutableLiveData<String>()
    var email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _timers = MutableLiveData<List<CountdownTimer>>()
    val timers: LiveData<List<CountdownTimer>> = _timers

    init {
        stateChangeListener()
    }

    private fun stateChangeListener() {
        viewModelScope.launch {
            firebaseRepository.auth.android.addAuthStateListener { firebaseAuth ->
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    _user.value = currentUser
                    _nav.value = PublicNav.Logged
                    viewModelScope.launch {
                        val userProfileResult = firebaseRepository.fetchUserProfile(currentUser.uid)
                        userProfileResult.onSuccess { userProfile ->
                            _userProfile.value = userProfile
                            userProfile?.profileImageURL?.let { profileImageURL ->
                                viewModelScope.launch {
                                    val imageResult = firebaseRepository.downloadProfileImage(profileImageURL)
                                    imageResult.onSuccess { bitmap ->
                                        bitmap?.let {
                                            if (it is Bitmap) {
                                                _userProfileImage.value = it
                                            }
                                        }
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
                    fetchTimers()
                }
                else {
                    _nav.value = PublicNav.Login
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
                firebaseRepository.saveUserProfile(userProfile).onSuccess {
                    Log.d("FirebaseAuthManager", "User profile saved successfully")
                }.onFailure {
                    Log.e("FirebaseAuthManager", "Error saving user profile", it)
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            firebaseRepository.signIn(email, password)
                .onSuccess {
                    _user.value = firebaseRepository.auth.android.currentUser
                }
                .onFailure {
                    Log.e("FirebaseAuthManager", "Error signing in", it)
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            firebaseRepository.signOut()

            _user.value = null
            _userProfile.value = null
            _userProfileImage.value = null
            _nav.value = PublicNav.Login
        }
    }

    fun signUp(email: String, password: String, complete: (Result<FirebaseUser?>) -> Unit) {
        viewModelScope.launch {
            val result = firebaseRepository.signOut()
            result.onSuccess {
                _nav.value = PublicNav.Login
            }.onFailure {
                // Sign-out failed, handle the error (e.g., show an error message)
            }
        }
    }

    fun uploadAndSaveProfileImage(bitmap: Bitmap) {
        viewModelScope.launch {
            firebaseRepository.uploadAndSaveProfileImage(bitmap).onSuccess {

            }.onFailure {

            }
        }
    }

    fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            firebaseRepository.fetchUserProfile(uid).onSuccess {
                _userProfile.value = it
                it?.profileImageURL?.let { profileImageURL ->
                    firebaseRepository.downloadProfileImage(profileImageURL)
                }
            }.onFailure {
                Log.e("FirebaseAuthManager", "Error fetching user profile", it)
            }
        }
    }

    fun navigateTo(view: PublicNav) {
        _nav.value = view
    }

    fun fetchTimers() {
        viewModelScope.launch {
            firebaseRepository.fetchTimers().onSuccess { timers ->
                _showSyn.value = true
                _timers.value = timers
                _showSyn.value = false
            }.onFailure {
                _showSyn.value = false
            }
        }
    }
}