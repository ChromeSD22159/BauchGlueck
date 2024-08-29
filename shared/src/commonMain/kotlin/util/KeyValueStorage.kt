package util

expect class KeyValueStorage {
    fun putString(key: String, value: String)

    fun putLong(key: String, value: Long)

    fun getString(key: String, defaultValue: String): String

    fun getLong(key: String, defaultValue: Long): Long

    fun getOrCreateDeviceId(): String
}