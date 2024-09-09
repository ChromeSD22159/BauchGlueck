package data.local

class LocalDataSource(
    db: LocalDatabase
) {
    val countdownTimer = db.timerDao
    val weight = db.weightDao
    val waterIntake = db.waterIntake
    val syncHistory = db.syncHistoryDao
    val medications = db.medicationDao
    val meals = db.mealDao
}