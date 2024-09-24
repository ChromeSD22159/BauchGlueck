package data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiUploadImageResponse(
    val id: Long,
    val name: String,
    val alternativeText: String?,  // Geändert zu String?
    val caption: String?,          // Geändert zu String?
    val width: Long,
    val height: Long,
    val formats: Formats?,         // Formate können unter Umständen nicht vorhanden sein
    val hash: String,
    val ext: String,
    val mime: String,
    val size: Double,
    val url: String,
    val previewUrl: String?,       // Geändert zu String?
    val provider: String,
    val createdAt: String,
    val updatedAt: String,
)