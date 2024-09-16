package util

import platform.Foundation.NSBundle

actual val appVersion: String
    get() {
        val nsObject = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString")
        return nsObject?.toString() ?: "Unbekannte Version"
    }