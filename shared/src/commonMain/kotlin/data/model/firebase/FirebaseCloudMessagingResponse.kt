package data.model.firebase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseCloudMessagingResponse(
    @SerialName("message") val message: String ?= null,
    @SerialName("response") val response: String ? = null,
)