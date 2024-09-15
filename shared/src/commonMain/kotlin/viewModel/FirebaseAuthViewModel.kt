package viewModel

import data.repositories.FirebaseRepository
import data.model.UserProfile
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging

class FirebaseAuthViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    private val _userFormState = MutableStateFlow(UserFormState())
    val userFormState = _userFormState.asStateFlow()

    val user get() = firebaseRepository.user

    init {
        authStateChanged()
    }

    private fun authStateChanged() {
        viewModelScope.launch {
            Firebase.auth.authStateChanged.collect {
                if (it != null) {
                    val profile = firebaseRepository.readUserProfileById(it.uid)
                    _userFormState.value = _userFormState.value.copy(currentUser = it,)

                    profile?.let {
                        _userFormState.value.userProfile.emit(it)
                    }
                } else {
                    _userFormState.value = _userFormState.value.copy(currentUser = null)
                }
            }
        }
    }

    fun onLogin() {
        viewModelScope.launch {
            _userFormState.value = _userFormState.value.copy(isProcessing = true, error = "")

            val errorMessage = validateInput(
                email = _userFormState.value.email,
                password = _userFormState.value.password
            )

            if (errorMessage != null) {
                _userFormState.value = _userFormState.value.copy(error = errorMessage, isProcessing = false)
                delay(5000)
                _userFormState.value = _userFormState.value.copy(error = "")
            }

            try {
                firebaseRepository.signIn(
                    _userFormState.value.email,
                    _userFormState.value.password
                ).user?.let {
                    resetLoginState()
                }
            } catch (e: Exception) {
                logging().e { "Error logging in: $e" }
                _userFormState.value = _userFormState.value.copy(
                    error = e.message ?: "Unknown error",
                    isProcessing = false
                )
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            firebaseRepository.signOut()
        }
    }

    fun onCancel() {
        _userFormState.value = UserFormState()
    }

    fun onSignUp(): Boolean {
        var result = false
        viewModelScope.launch {
            _userFormState.value = _userFormState.value.copy(isProcessing = true)

            val errorMessage = validateInput(
                firstName = _userFormState.value.firstName,
                email = _userFormState.value.email,
                password = _userFormState.value.password,
                confirmPassword = _userFormState.value.confirmPassword
            )

            if (errorMessage != null) {
                _userFormState.value = _userFormState.value.copy(error = errorMessage, isProcessing = false)
                delay(5000)
                _userFormState.value = _userFormState.value.copy(error = "")
                return@launch
            }

            val newUserProfile = UserProfile(
                firstName = _userFormState.value.firstName,
                email = _userFormState.value.email,
                surgeryDateTimeStamp = Clock.System.now().toEpochMilliseconds(),
                mainMeals = 3,
                betweenMeals = 3,
                profileImageURL = "",
                startWeight = 80.0,
                waterIntake = 200.0,
                waterDayIntake = 2000.0
            )

            val error = firebaseRepository.createUserWithEmailAndPassword(newUserProfile, _userFormState.value.password)

            if (error != null) {
                resetLoginState()
                onLogout()
                result = true
            } else {
                result = false
                _userFormState.value = _userFormState.value.copy(error = "", isProcessing = false)
            }
        }
        return result
    }

    fun onChangeFirstName(firstName: String) {
        _userFormState.value = _userFormState.value.copy(firstName = firstName)
    }

    fun onChangeEmail(email: String) {
        _userFormState.value = _userFormState.value.copy(email = email)
    }

    fun onChangePassword(password: String) {
        _userFormState.value = _userFormState.value.copy(password = password)
    }

    fun onChangeConfirmPassword(confirmPassword: String) {
        _userFormState.value = _userFormState.value.copy(confirmPassword = confirmPassword)
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            firebaseRepository.forgotPassword(email)
        }
    }

    fun onUpdateUserProfile(
        userProfile: UserProfile
    ) {
        viewModelScope.launch {
            _userFormState.value.userProfile.emit(userProfile)
            firebaseRepository.saveUserProfile(userProfile)
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String? = null,
        firstName: String? = null,
    ): String? {
        return when {
            email.isEmpty() -> {
                "Please enter an email"
            }
            password.isEmpty() -> {
                "Please enter a password"
            }
            confirmPassword != null && password != confirmPassword -> {
                "Passwords do not match"
            }
            firstName != null && firstName.isEmpty() -> {
                "Please enter a first name"
            }
            else -> null
        }
    }

    private fun resetLoginState() {
        _userFormState.update {
            it.copy(
                isProcessing = false,
                error = "",
                email = "",
                password = "",
                confirmPassword = "",
                firstName = "",
            )
        }
    }
}

data class UserFormState(
    val firstName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isProcessing: Boolean = false,
    val userProfile: MutableStateFlow<UserProfile> = MutableStateFlow(UserProfile()),
    val currentUser: FirebaseUser? = null,
    val error: String = ""
)



