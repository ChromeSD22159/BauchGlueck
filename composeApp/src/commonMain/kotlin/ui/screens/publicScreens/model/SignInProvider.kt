package ui.screens.publicScreens.model

import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_apple
import bauchglueck.composeapp.generated.resources.ic_google
import org.jetbrains.compose.resources.DrawableResource

enum class SignInProvider(var icon: DrawableResource) {
    Google(Res.drawable.ic_google),
    Apple(Res.drawable.ic_apple)
}