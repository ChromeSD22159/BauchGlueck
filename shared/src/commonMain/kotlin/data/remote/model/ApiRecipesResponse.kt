package data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiRecipesResponse(
    @SerialName("id") val id: Int,
    @SerialName("updatedAtOnDevice") val updatedAtOnDevice: Long? = null,
    @SerialName("mealId") val mealId: String,
    @SerialName("userId") val userId: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("isSnack") val isSnack: Boolean,
    @SerialName("isPrivate") val isPrivate: Boolean,
    @SerialName("isDeleted") val isDeleted: Boolean,
    @SerialName("preparation") val preparation: String,
    @SerialName("ingredients") val ingredients: List<Ingredient>,
    @SerialName("mainImage") val mainImage: MainImage,
    @SerialName("category") val category: Category
)

@Serializable
data class Ingredient(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: String,
    @SerialName("unit") val unit: String
)

@Serializable
data class MainImage(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("alternativeText") val alternativeText: String? = null,
    @SerialName("caption") val caption: String? = null,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("formats") val formats: Formats,
    @SerialName("hash") val hash: String,
    @SerialName("ext") val ext: String,
    @SerialName("mime") val mime: String,
    @SerialName("size") val size: Double,
    @SerialName("url") val url: String,
    @SerialName("previewUrl") val previewUrl: String? = null,
    @SerialName("provider") val provider: String,
    @SerialName("provider_metadata") val providerMetadata: String? = null,
    @SerialName("folderPath") val folderPath: String
)

@Serializable
data class Formats(
    @SerialName("thumbnail") val thumbnail: ImageFormat,
    @SerialName("small") val small: ImageFormat
)

@Serializable
data class ImageFormat(
    @SerialName("name") val name: String,
    @SerialName("hash") val hash: String,
    @SerialName("ext") val ext: String,
    @SerialName("mime") val mime: String,
    @SerialName("path") val path: String? = null,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("size") val size: Double,
    @SerialName("sizeInBytes") val sizeInBytes: Int,
    @SerialName("url") val url: String
)

@Serializable
data class Category(
    @SerialName("id") val id: Int,
    @SerialName("categoryId") val categoryId: String,
    @SerialName("name") val name: String
)