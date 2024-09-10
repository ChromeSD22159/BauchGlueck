package data.local.entitiy

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


@Entity(tableName = "mealPlanDay")
data class MealPlanDay(
    @PrimaryKey val mealPlanDayId: String = "", // KPYT-EPTZ-SADL-FTLS
    val userId: String = "",
    val date: String = "",
    val isDeleted: Boolean = false,
    val updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds()
)


@Entity(
    tableName = "mealPlanSpot",
    foreignKeys = [
        ForeignKey(
            entity = MealPlanDay::class,
            parentColumns = ["mealPlanDayId"],
            childColumns = ["mealPlanDayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["mealId"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mealPlanDayId"), Index("mealId")]
)
data class MealPlanSpot(
    @PrimaryKey val mealPlanSpotId: String = "",
    var mealPlanDayId: String = "",
    val mealId: String = "",
    val userId: String = "",
    val timeSlot: String = "",
    val isDeleted: Boolean = false,
    var meal: String? = null,
    val updatedAtOnDevice: Long = Clock.System.now().toEpochMilliseconds()
) {
    var mealObject: Meal?
        get() = try {
            meal?.let {
                Json.decodeFromString(it)
            }
        } catch (e: Exception) {
            null
        }
        set(value) {
            meal = if(value != null) { Json.encodeToString(value) } else { null  }
        }
}


data class MealPlanDayWithSpots(
    @Embedded val mealPlanDay: MealPlanDay,
    @Relation(
        parentColumn = "mealPlanDayId",      // In MealPlanDay
        entityColumn = "mealPlanDayId"       // In MealPlanSpot
    )
    val spots: List<MealPlanSpot>
)

data class MealPlanSpotWithMeal(
    @Embedded val mealPlanSpot: MealPlanSpot,
    @Relation(
        parentColumn = "mealId",             // In MealPlanSpot
        entityColumn = "mealId"              // In Meal
    )
    val meal: Meal
)