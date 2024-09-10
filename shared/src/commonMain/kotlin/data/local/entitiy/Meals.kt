package data.local.entitiy

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation
import data.remote.model.Ingredient
import data.remote.model.MainImage
import kotlinx.serialization.json.Json


@Entity(
    tableName = "meals",
    primaryKeys = ["mealId"],
    foreignKeys = [
        ForeignKey(
            entity = MealCategory::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Meal(
    var id: Int = 0,
    var mealId: String, // PK
    var updatedAtOnDevice: Long?,
    var userId: String,
    var name: String,
    var description: String,
    var isSnack: Boolean,
    var isPrivate: Boolean,
    var isDeleted: Boolean,
    var preparation: String,
    var preparationTimeInMinutes: Int,
    var protein: Double,
    var fat: Double,
    var sugar: Double,
    var kcal: Double,
    var ingredientsString: String = "[]",
    var mainImageString: String = "{}",
    var categoryId: String? = null, // FK -> 10
) {
    val ingredients: List<Ingredient>
        get() = try {
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString(ingredientsString)
        } catch (e: Exception) {
            emptyList()
        }

    var mainImage: MainImage?
        get() = try {
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromString(mainImageString)
        } catch (e: Exception) {
            null
        }
        set(value) { }
}



@Entity(
    tableName = "categories",
    primaryKeys = ["categoryId"],
)
data class MealCategory(
    var categoryId: String, // PK -> 10
    var name: String,
) {
    fun toCategoryEntity(): MealCategory {
        return MealCategory(
            categoryId = categoryId,
            name = name
        )
    }
}


@Entity(
    tableName = "meal_category_cross_ref",
    primaryKeys = ["mealId", "categoryId"],
    indices = [Index("mealId"), Index("categoryId")],
    foreignKeys = [
        ForeignKey(entity = Meal::class, parentColumns = ["mealId"], childColumns = ["mealId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MealCategory::class, parentColumns = ["categoryId"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class MealCategoryCrossRef(
    val mealId: String,
    val categoryId: String
)


data class CategoryWithMeals(
    @Embedded val mealCategory: MealCategory,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "mealId",
        associateBy = Junction(MealCategoryCrossRef::class)
    )
    val meals: List<Meal>
)


data class MealWithCategories(
    @Embedded val meal: Meal,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "categoryId",
        associateBy = Junction(MealCategoryCrossRef::class)
    )
    val categories: List<MealCategory>
)