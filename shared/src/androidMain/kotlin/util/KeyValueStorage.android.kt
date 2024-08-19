package util

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

actual class KeyValueStorage(context: Context) {

    private val settings: Settings = SharedPreferencesSettings(
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    )

    actual fun putString(key: String, value: String) {
        settings[key] = value
    }

    actual fun getString(key: String, defaultValue: String): String {
        return settings[key, defaultValue]
    }

    actual fun getOrCreateDeviceId(): String {
        val deviceId = getString("deviceId", "")

        if (deviceId == "") {
            val newDeviceId = generateDeviceId()
            putString("deviceId", newDeviceId)
            return newDeviceId
        } else {
            return deviceId
        }
    }
}