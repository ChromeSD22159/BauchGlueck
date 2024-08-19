package util

expect class KeyValueStorage {
    fun putString(key: String, value: String)

    fun getString(key: String, defaultValue: String): String

    fun getOrCreateDeviceId(): String
}