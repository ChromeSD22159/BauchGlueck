package data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import data.local.dao.CountdownTimerDao
import data.local.dao.MedicationDao
import data.local.dao.SyncHistoryDao
import data.local.dao.WaterIntakeDao
import data.local.dao.WeightDao
import data.local.entitiy.CountdownTimer
import data.local.entitiy.Medication
import data.local.entitiy.IntakeTimes
import data.local.entitiy.MedicationWithIntakeTimes
import data.local.entitiy.SyncHistory
import data.local.entitiy.WaterIntake
import data.local.entitiy.Weight

@Database(
    entities = [
        CountdownTimer::class,
        SyncHistory::class,
        Weight::class,
        WaterIntake::class,
        Medication::class,
        IntakeTimes::class,
        //MealPlan::class,
        //Recipe::class
   ],
    version = 2,
    exportSchema = false
)
abstract class LocalDatabase: RoomDatabase(), DB {
    abstract val timerDao: CountdownTimerDao
    abstract val syncHistoryDao: SyncHistoryDao
    abstract val weightDao: WeightDao
    abstract val waterIntake: WaterIntakeDao
    abstract val medicationDao: MedicationDao
    //abstract val mealPlanDao: MealPlanDao
    //abstract val recipeDao: RecipesDao

     override fun clearAllTables() {
         super.clearAllTables()
    }
}

internal const val dbFileName = "LocalDatabase.db"


interface DB {
    fun clearAllTables() {}
}

enum class RoomTable(val tableName: String) {
    COUNTDOWN_TIMER("countdownTimer"),
    SYNC_HISTORY("syncHistory"),
    WEIGHT("weight"),
    WATER_INTAKE("waterIntake"),
    MEDICATION("medication");
    //MEAL_PLAN("mealPlan"),
    //RECIPE("recipe")

    fun getTableName(name: String): RoomTable {
        return RoomTable.valueOf(name)
    }
}