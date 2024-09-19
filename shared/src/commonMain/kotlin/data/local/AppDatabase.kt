package data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import data.local.dao.ChangeLogDao
import data.local.dao.CountdownTimerDao
import data.local.dao.MealDao
import data.local.dao.MealPlanDao
import data.local.dao.MedicationDao
import data.local.dao.NodeDao
import data.local.dao.ShoppingListDao
import data.local.dao.SyncHistoryDao
import data.local.dao.WaterIntakeDao
import data.local.dao.WeightDao
import data.local.entitiy.ChangeLog
import data.local.entitiy.CountdownTimer
import data.local.entitiy.IntakeStatus
import data.local.entitiy.IntakeTime
import data.local.entitiy.Medication
import data.local.entitiy.SyncHistory
import data.local.entitiy.WaterIntake
import data.local.entitiy.Weight
import data.local.entitiy.Meal
import data.local.entitiy.MealPlanDay
import data.local.entitiy.MealPlanSpot
import data.local.entitiy.MealCategory
import data.local.entitiy.MealCategoryCrossRef
import data.local.entitiy.Node
import data.local.entitiy.ShoppingList

@Database(
    entities = [
        CountdownTimer::class,
        SyncHistory::class,
        Weight::class,
        WaterIntake::class,
        Medication::class,
        IntakeTime::class,
        IntakeStatus::class,
        Meal::class,
        MealCategory::class,
        MealCategoryCrossRef::class,
        MealPlanDay::class,
        MealPlanSpot::class,
        ShoppingList::class,
        ChangeLog::class,
        Node::class
   ],
    version = 11,
    exportSchema = false
)
abstract class LocalDatabase: RoomDatabase(), DB {
    abstract val timerDao: CountdownTimerDao
    abstract val syncHistoryDao: SyncHistoryDao
    abstract val weightDao: WeightDao
    abstract val waterIntake: WaterIntakeDao
    abstract val medicationDao: MedicationDao
    abstract val mealDao: MealDao
    abstract val mealPlanDao: MealPlanDao
    abstract val shoppingListDao: ShoppingListDao
    abstract val changeLogDao: ChangeLogDao
    abstract val nodeDao: NodeDao

     override fun clearAllTables() {
         super.clearAllTables()
    }
}

internal const val dbFileName = "LocalDatabase.db"


interface DB {
    fun clearAllTables() {}
}