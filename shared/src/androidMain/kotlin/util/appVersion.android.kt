package util

import android.content.Context
import android.content.pm.PackageManager

actual val appVersion: String
    get() {
        val context = ApplicationContextHolder.context
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unbekannte Version"
        } catch (e: Exception) {
            "Unbekannte Version"
        }
    }

object ApplicationContextHolder {
    lateinit var context: Context
}