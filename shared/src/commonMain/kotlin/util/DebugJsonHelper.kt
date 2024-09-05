package util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging

inline fun <reified T> debugJsonHelper(entity: T) {
    val jsonFormatter = Json {
        prettyPrint = true // For easier readability
        encodeDefaults = true // Encode default values
    }
    val jsonData = jsonFormatter.encodeToString(entity)
    logging().info { "JSON Data: $jsonData" }
}