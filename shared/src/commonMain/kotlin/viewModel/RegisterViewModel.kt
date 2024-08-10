package viewModel

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.flow.cFlow
import dev.icerock.moko.mvvm.flow.cMutableStateFlow
import dev.icerock.moko.mvvm.flow.cStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class RegisterViewModel : ViewModel() {
    var firstName: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val lastName: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val mail: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val password: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val reEnterPassword: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()

    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow().cStateFlow()

    val isButtonEnabled: StateFlow<Boolean> = combine(firstName, lastName, mail, password) { firstName, lastName, mail, password ->
        firstName.isNotBlank() &&
        lastName.isNotBlank() &&
        mail.isNotBlank() &&
        isValidPassword.value
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false).cStateFlow()

    private val isValidPassword: StateFlow<Boolean> = combine(reEnterPassword, password) { reEnterPassword, password ->
        reEnterPassword == password
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false).cStateFlow()

    private val _actions = Channel<Action>()
    val actions = _actions.receiveAsFlow().cFlow()

    fun onRegisterButtonPressed(action: (RegisterState) -> RegisterState) {
        _isProcessing.value = true
        viewModelScope.launch {
            //  delay(1000)
            val registerState = action(
                RegisterState(
                    firstName = this@RegisterViewModel.firstName.value,
                    lastName = this@RegisterViewModel.lastName.value,
                    surveryDate = LocalDate(2023, 1, 1),
                    mail = this@RegisterViewModel.mail.value,
                    password = this@RegisterViewModel.password.value,
                    isSignedIn = false
                )
            )

            if (registerState.isSignedIn) {
                _actions.send(Action.RegisterSuccess)
            } else {
                _actions.send(Action.RegisterError)
            }

            _isProcessing.value = false
        }
    }

    fun onCancelButtonPressed() {
        _isProcessing.value = true
        viewModelScope.launch {
            delay(1000)//simulate api
            _isProcessing.value = false
            _actions.send(Action.Cancel)
        }
    }

    fun updateFirstName(newEmail: String) {
        firstName.value = newEmail
    }

    fun updateLastName(newEmail: String) {
        lastName.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    sealed class Action(val message: String) {
        data object LoginError : Action("Login Error")
        data object RegisterSuccess : Action("Register Success")
        data object RegisterError : Action("Register Error")
        data object ForgotPassword : Action("Forgot Password")
        data object ForgotPasswordSuccess : Action("Forgot Password Success")
        data object ForgotPasswordError : Action("Forgot Password Error")
        data object ResetPasswordSuccess : Action("Reset Password Success")
        data object ResetPasswordError : Action("Reset Password Error")
        data object LogoutSuccess : Action("Logout Success")
        data object LogoutError : Action("Logout Error")
        data object Cancel : Action("Cancel")
    }
}

data class RegisterState(
    val firstName: String,
    val lastName: String,
    val surveryDate: LocalDate,
    val mail: String,
    val password: String,
    var isSignedIn: Boolean
)