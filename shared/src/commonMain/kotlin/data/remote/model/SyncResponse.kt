package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    val message: String
)