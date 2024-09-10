package data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Serializable
data class ApiMealPlanDayResponse(
    val updatedAtOnDevice: String,
    val mealPlanDayId: String,
    val userId: String,
    val date: String,
    val isDeleted: Boolean,
    val mealPlanSlots: List<MealPlanSlot>,
) {
    fun toMealPlanDay(): data.local.entitiy.MealPlanDay {
        return data.local.entitiy.MealPlanDay(
            mealPlanDayId = this.mealPlanDayId,
            userId = this.userId,
            date = this.date,
            isDeleted = this.isDeleted,
            updatedAtOnDevice = this.updatedAtOnDevice.toLong(),
        )
    }
}

@Serializable
data class MealPlanSlot(
    val mealPlanSlotId: String,
    val userId: String,
    val timeSlot: String,
    val isDeleted: Boolean,
    val updatedAtOnDevice: Long,
    val meal: Meal,
) {
    fun toRoomMealPlanSlot(): data.local.entitiy.MealPlanSpot {
        return data.local.entitiy.MealPlanSpot(
            mealPlanDayId = "",
            mealPlanSpotId = this.mealPlanSlotId,
            mealId = this.meal.mealId,
            userId = this.userId,
            meal = Json.encodeToString(this.meal),
            timeSlot = this.timeSlot,
            isDeleted = this.isDeleted,
            updatedAtOnDevice = this.updatedAtOnDevice,
        )
    }
}

@Serializable
data class Meal(
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
    @SerialName("ingredients") var ingredients: List<Ingredient>,
    @SerialName("mainImage") val mainImage: MainImage,
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
