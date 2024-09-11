package util

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import platform.Foundation.NSUserDefaults

actual class KeyValueStorage {

    private val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults()
    private val settings: Settings = NSUserDefaultsSettings(delegate)

    actual fun putString(key: String, value: String) {
        settings[key] = value
    }

    actual fun putLong(key: String, value: Long) {
        settings[key] = value
    }

    actual fun getString(key: String, defaultValue: String): String {
        return settings[key, defaultValue]
    }

    actual fun getLong(key: String, defaultValue: Long): Long {
        return settings[key, defaultValue]
    }

    actual fun getOrCreateDeviceId(): String {
        val deviceId = getString("deviceId", "")

        if (deviceId == "") {
            val newDeviceId = UUID.randomUUID()
            putString("deviceId", newDeviceId)
            return newDeviceId
        } else {
            return deviceId
        }
    }
}