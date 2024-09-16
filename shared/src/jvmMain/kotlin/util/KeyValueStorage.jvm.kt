package util

actual class KeyValueStorage {
    actual fun putString(key: String, value: String) {
    }

    actual fun getString(key: String, defaultValue: String): String {
        TODO("Not yet implemented")
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