package de.frederikkohler.bauchglueck.viewModel

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import data.FirebaseConnection
import de.frederikkohler.bauchglueck.data.network.FirebaseRepository
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import model.UserProfile
import model.countdownTimer.CountdownTimer

class FirebaseAuthViewModel(
    private val firebaseRepository: FirebaseRepository = FirebaseRepository()
): ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(null).cMutableStateFlow()
    val user: CMutableStateFlow<FirebaseUser?> = _user.cMutableStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null).cMutableStateFlow()
    val userProfile: CMutableStateFlow<UserProfile?> = _userProfile.cMutableStateFlow()

    private val _userProfileImage = MutableStateFlow<Bitmap?>(null).cMutableStateFlow()
    val userProfileImage: CMutableStateFlow<Bitmap?> = _userProfileImage.cMutableStateFlow()

    private val _showSyn = MutableStateFlow(false).cMutableStateFlow()
    val showSyn: CMutableStateFlow<Boolean> = _showSyn.cMutableStateFlow()

    private val _email: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    var email: CMutableStateFlow<String> = _email.cMutableStateFlow()

    private val _password: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val password: CMutableStateFlow<String> = _password.cMutableStateFlow()

    private val _timers = MutableStateFlow<List<CountdownTimer>>(emptyList()).cMutableStateFlow()
    val timers: CMutableStateFlow<List<CountdownTimer>> = _timers.cMutableStateFlow()

    init {
        stateChangeListener()
    }

    private fun stateChangeListener() {
        viewModelScope.launch {
            firebaseRepository.auth.android.addAuthStateListener { firebaseAuth ->
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    _user.value = currentUser
                    viewModelScope.launch {
                        firebaseRepository.setUserOnline()
                    }
                    fetchUserProfile(currentUser.uid)
                    fetchTimers()
                }
                else {
                    viewModelScope.launch {
                        firebaseRepository.setUserOffline()
                    }
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

    fun saveUserProfile(connection: FirebaseConnection = FirebaseConnection.Local) {
        viewModelScope.launch {
            _userProfile.value?.let { userProfile ->
                firebaseRepository.saveUserProfile(userProfile, connection).onSuccess {
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
        }
    }

    fun signUp(email: String, password: String, complete: (Result<FirebaseUser?>) -> Unit) {
        viewModelScope.launch {
            val result = firebaseRepository.signOut()
            result.onSuccess {
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
                Log.i("FirebaseAuthManager", "Fetched user profile: ${it.toString()}")
                it?.profileImageURL?.let { profileImageURL ->
                    firebaseRepository.downloadProfileImage(profileImageURL)
                }
            }.onFailure {
                Log.e("FirebaseAuthManager", "Error fetching user profile", it)
            }
        }
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

    fun setUserOnline() {
        viewModelScope.launch {
            FirebaseRepository().setUserOnline()
        }
    }

    fun setUserOffline() {
        viewModelScope.launch {
            FirebaseRepository().setUserOffline()
        }
    }

    fun updateUserProfile(userProfile: UserProfile) {
        _userProfile.value = userProfile
    }

    fun syncFirebase() {
        viewModelScope.launch {
            firebaseRepository.sync()
        }
    }
}