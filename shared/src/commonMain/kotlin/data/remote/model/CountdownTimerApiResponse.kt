package data.remote.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable
data class CountdownTimerAttributes(
    val timerId: String,
    val userId: String,
    val name: String,
    val duration: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val timerState: String,
    val showActivity: Boolean,
    val createdAt: String,
    val updatedAt: String
)