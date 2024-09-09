package data.local.entitiy

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "mealPlanDay",
    primaryKeys = ["mealPlanId"],
)
data class MealPlanDay(
    val mealPlanId: String,
    val userId: String,
    val date: String,
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = 0
)
@Serializable
@Entity(
    tableName = "mealPlanSpot",
    primaryKeys = ["mealPlanDayId", "mealId"],  // Composite primary key
    foreignKeys = [
        ForeignKey(
            entity = MealPlanDay::class,
            parentColumns = ["mealPlanId"],
            childColumns = ["mealPlanDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MealPlanSpot(
    val mealPlanDayId: String,
    val mealId: String,
    val userId: String,
    val timeSlot: String,
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long? = 0
)

data class MealPlanDayWithSpots(
    @Embedded val mealPlanDay: MealPlanDay,
    @Relation(
        parentColumn = "mealPlanId",
        entityColumn = "mealPlanDayId"
    )
    val spots: List<MealPlanSpotWithMeal>
)
data class MealPlanSpotWithMeal(
    @Embedded val mealPlanSpot: MealPlanSpot,
    @Relation(
        parentColumn = "mealId",
        entityColumn = "mealId"
    )
    val meal: Meal?
)