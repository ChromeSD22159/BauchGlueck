package de.frederikkohler.bauchglueck.ui.navigations

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

object NavigationTransition {
    fun slideInWithFadeToTopAnimation(): EnterTransition {
        return slideInVertically(
            initialOffsetY = { it.takeIf { it != Int.MIN_VALUE } ?: 0 },
            animationSpec = tween(250)
        ) + scaleIn(
            animationSpec = tween(250)
        ) + fadeIn(animationSpec = tween(250))
    }

    fun slideOutWithFadeToTopAnimation(): ExitTransition {
        return slideOutVertically(
            targetOffsetY = { it.takeIf { it != Int.MIN_VALUE } ?: 0 },
            animationSpec = tween(250)
        ) + scaleOut(
            animationSpec = tween(250)
        ) + fadeOut(animationSpec = tween(250))
    }
}