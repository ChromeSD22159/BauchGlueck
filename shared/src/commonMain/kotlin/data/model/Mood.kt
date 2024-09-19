package data.model

import kotlinx.serialization.Serializable

@Serializable
data class Mood(val display: String, var isOnList: Boolean = false)