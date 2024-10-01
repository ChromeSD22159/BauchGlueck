package util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import util.ApplicationContextHolder.context

// Helper function to get the current Activity from a context
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}


actual fun hideKeyboard(context: Any) {
    val ctx = context as? Context ?: return
    val activity = ctx.findActivity()

    activity?.currentFocus?.let { view ->
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}