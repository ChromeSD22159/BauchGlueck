package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiDeviceToken(
    var userID: String,
    var token: String
)