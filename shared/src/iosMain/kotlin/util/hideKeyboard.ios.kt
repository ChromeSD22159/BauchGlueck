package util

import platform.UIKit.UIApplication
import platform.UIKit.endEditing

actual fun hideKeyboard(context: Any) {
    val keyWindow = UIApplication.sharedApplication.keyWindow
    keyWindow?.endEditing(true)
}