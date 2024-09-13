package viewModel

import data.repositories.FirebaseRepository
import data.model.UserProfile
import dev.gitlive.firebase.auth.FirebaseUser
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class FirebaseAuthViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()

    private val _userFormState = MutableStateFlow(UserFormState())
    val userFormState = _userFormState.asStateFlow()

    init {
        viewModelScope.launch {
            _userFormState.value = _userFormState.value.copy(userProfile = firebaseRepository.readUserProfile())

            logging().info { "UserFormState: ${firebaseRepository.readUserProfile()}" }
        }
    }

    fun onLogin() {
        viewModelScope.launch {
            _userFormState.value = _userFormState.value.copy(isProcessing = true)
            val response = firebaseRepository.signIn(_userFormState.value.email, _userFormState.value.password)
            _userFormState.value = _userFormState.value.copy(currentUser = response.user, isProcessing = false)
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

    fun onSignUp() {
        viewModelScope.launch {
            _userFormState.value = _userFormState.value.copy(isProcessing = true)
            if (_userFormState.value.password == _userFormState.value.confirmPassword) {

                val newUserProfile = UserProfile(
                    firstName = _userFormState.value.firstName,
                    lastName = _userFormState.value.lastName,
                    email = _userFormState.value.email,
                    surgeryDateTimeStamp = 0,
                    mainMeals = 0,
                    betweenMeals = 0,
                    profileImageURL = "",
                    startWeight = 0.0,
                    waterIntake = 0.0,
                    waterDayIntake = 0.0
                )

                val hasError = firebaseRepository.createUserWithEmailAndPassword(newUserProfile, userFormState.value.password)

                if (hasError != null) {
                    _userFormState.value = _userFormState.value.copy(error = hasError.message ?: "Unknown error")
                } else {
                    _userFormState.value = _userFormState.value.copy(error = "")
                }
            } else {
                _userFormState.value = _userFormState.value.copy(error = "Passwords do not match")
            }

            _userFormState.value = _userFormState.value.copy(isProcessing = false)
        }
    }

    fun onChangeFirstName(firstName: String) {
        _userFormState.value = _userFormState.value.copy(firstName = firstName)
    }

    fun onChangeLastName(lastName: String) {
        _userFormState.value = _userFormState.value.copy(lastName = lastName)
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

    fun onUpdateUserProfile(
        userProfile: UserProfile
    ) {
        viewModelScope.launch {
            firebaseRepository.saveUserProfile(userProfile)
        }
    }
}

data class UserFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isProcessing: Boolean = false,
    val userProfile: UserProfile? = null,
    val currentUser: FirebaseUser? = null,
    val error: String = ""
)



