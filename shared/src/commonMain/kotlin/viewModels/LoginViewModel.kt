package viewModels

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

class LoginViewModel : ViewModel() {
    val mail: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()
    val password: CMutableStateFlow<String> = MutableStateFlow("").cMutableStateFlow()

    private val _isProcessing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow().cStateFlow()

    val isButtonEnabled: StateFlow<Boolean> = combine(mail, password) { username, password ->
        username.isNotBlank() && password.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false).cStateFlow()

    private val _actions = Channel<Action>()
    val actions = _actions.receiveAsFlow().cFlow()

    fun onLoginButtonPressed(action: (LoginState) -> LoginState) {
        _isProcessing.value = true
        viewModelScope.launch {
            //  delay(1000)

            val loginResult = action(
                LoginState(
                    mail = this@LoginViewModel.mail.value,
                    password = this@LoginViewModel.password.value,
                    isSignedIn = false
                )
            )

            if (loginResult.isSignedIn) {
                _actions.send(Action.LoginSuccess)
            } else {
                _actions.send(Action.LoginError)
            }

            _isProcessing.value = false
        }
    }

    sealed class Action(val message: String) {
        data object LoginSuccess : Action("Login Success")
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

data class LoginState (
    val mail: String,
    val password: String,
    var isSignedIn: Boolean = false
)

