package data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
    @SerialName("preparationTimeInMinutes") val preparationTimeInMinutes: Int,
    @SerialName("ingredients") var ingredients: List<Ingredient> = emptyList(),
    @SerialName("mainImage") val mainImage: MainImage? = null,
    @SerialName("category") val category: Category,
    @SerialName("protein") val protein: Double = 0.0,
    @SerialName("fat") val fat: Double = 0.0,
    @SerialName("sugar") val sugar : Double = 0.0,
    @SerialName("kcal") val kcal: Double = 0.0,
) {
    var ingredientsString: String
        get() {
            return Json.encodeToString(this.ingredients)
        }
        set(_) {}

    var mainImageString: String
        get() {
            return Json.encodeToString(this.mainImage)
        }
        set(_) {}

    fun toRoomMeal(): data.local.entitiy.Meal {
        return data.local.entitiy.Meal(
            id = this.id,
            mealId = this.mealId,
            userId = this.userId,
            name = this.name,
            description = this.description,
            isSnack = this.isSnack,
            isPrivate = this.isPrivate,
            isDeleted = this.isDeleted,
            preparation = this.preparation,
            preparationTimeInMinutes = this.preparationTimeInMinutes,
            ingredientsString = this.ingredientsString,
            mainImageString = this.mainImageString,
            protein = this.protein,
            fat = this.fat,
            sugar = this.sugar,
            kcal = this.kcal,
            updatedAtOnDevice = this.updatedAtOnDevice,
            categoryId = this.category.categoryId
        )
    }
}

@Serializable
data class Ingredient(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: String,
    @SerialName("unit") var unit: String
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
    @SerialName("xsmall") val xsmall: ImageFormat,
    @SerialName("small") val small: ImageFormat,
    @SerialName("medium") val medium: ImageFormat,
    @SerialName("large") val large: ImageFormat? = null,
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
    @SerialName("categoryId") var categoryId: String,
    @SerialName("name") var name: String
) {
    fun toRoomCategory(): data.local.entitiy.MealCategory {
        return data.local.entitiy.MealCategory(
            categoryId = this.categoryId,
            name = this.name
        )
    }
}