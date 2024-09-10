package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiChangeLog(
    val versionNumber: String = "",
    val releaseDate: String = "",
    val features: List<ChangeLogItem> = emptyList()
)

@Serializable
data class ChangeLogItem(
    val value: String = ""
)