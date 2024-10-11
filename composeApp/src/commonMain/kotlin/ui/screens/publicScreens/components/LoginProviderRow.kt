package ui.screens.publicScreens.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.mmk.kmpauth.uihelper.apple.AppleButtonMode
import com.mmk.kmpauth.uihelper.apple.AppleSignInButtonIconOnly
import com.mmk.kmpauth.uihelper.google.GoogleButtonMode
import com.mmk.kmpauth.uihelper.google.GoogleSignInButtonIconOnly
import org.lighthousegames.logging.logging
import ui.navigations.Destination
import viewModel.FirebaseAuthViewModel

@Composable
fun LoginProviderRow(
    firebaseAuthViewModel: FirebaseAuthViewModel,
    service: String = "842780509257-talp7l10pf0jkolerevtqhppbg6dnddr.apps.googleusercontent.com",
    onNavigate: (Destination, FirebaseUserId) -> Unit
) {
    val authReady = remember { mutableStateOf(false) }
    LaunchedEffect(authReady) {
        GoogleAuthProvider.create(
            credentials =  GoogleAuthCredentials( service )
        )

        authReady.value = true
    }
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (authReady.value) {
            GoogleButtonUiContainerFirebase(
                onResult = { result  ->
                    result.onSuccess {
                        val tokenId = it?.uid
                        if (tokenId != null) {
                            firebaseAuthViewModel.firebaseUserExist(user = it) { foundProfile ->
                                if (!foundProfile) {
                                    onNavigate(Destination.CreateUserProfile, FirebaseUserId(tokenId))
                                } else {
                                    onNavigate(Destination.Home, FirebaseUserId(tokenId))
                                }
                            }
                        }
                    }
                }
            ) {
                GoogleSignInButtonIconOnly(
                    mode = if(isSystemInDarkTheme()) GoogleButtonMode.Dark else GoogleButtonMode.Light,
                    onClick = { this.onClick() }
                )
            }
        }

        AppleButtonUiContainer(
            requestScopes = listOf(),
            onResult = { result ->
                result.onSuccess {
                    val tokenId = it?.uid
                    if (tokenId != null) {
                        firebaseAuthViewModel.firebaseUserExist(user = it) { foundProfile ->
                            if (!foundProfile) {
                                onNavigate(Destination.CreateUserProfile, FirebaseUserId(tokenId))
                            } else {
                                onNavigate(Destination.Home, FirebaseUserId(tokenId))
                            }
                        }
                    }
                }.onFailure {
                    if (it.message != null) {
                        logging().info { it.message }
                    }
                }
            }
        ) {
            AppleSignInButtonIconOnly(
                mode = if(isSystemInDarkTheme()) AppleButtonMode.Black else AppleButtonMode.WhiteWithOutline,
                onClick = { this.onClick() }
            )
        }
    }
}

data class FirebaseUserId(val id: String)