package data.local

enum class RoomTable(val tableName: String) {
    COUNTDOWN_TIMER("countdownTimer"),
    SYNC_HISTORY("syncHistory"),
    WEIGHT("weight"),
    WATER_INTAKE("waterIntake"),
    MEDICATION("medication"),
    Meal("meal"),
    MEAL_PLAN("mealPlan");

    fun getTableName(name: String): RoomTable {
        return RoomTable.valueOf(name)
    }
}