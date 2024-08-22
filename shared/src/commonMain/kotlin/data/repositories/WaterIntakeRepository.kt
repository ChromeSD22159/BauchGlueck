package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.WaterIntakeDao
import data.local.entitiy.WaterIntake
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock

class WaterIntakeRepository(
    db: LocalDatabase,
    private var user: FirebaseUser? = Firebase.auth.currentUser
) {
    private var waterIntake: WaterIntakeDao = LocalDataSource(db).waterIntake

    suspend fun getAllWaterIntakes(): List<WaterIntake> {
        return if (user == null) emptyList() else waterIntake.getAllWaterIntakes(user!!.uid)
    }

    suspend fun getWaterIntakeById(id: Int): WaterIntake? = waterIntake.getWaterIntakesById(id)

    suspend fun insertOrUpdateWaterIntake(waterIntake: WaterIntake) = this.waterIntake.insertOrUpdateWaterIntake(waterIntake)

    suspend fun insertOrUpdateWaterIntakes(waterIntakes: List<WaterIntake>) {
        waterIntakes.forEach {
            this.waterIntake.insertOrUpdateWaterIntake(it)
        }
    }

    suspend fun updateWaterIntakes(waterIntakes: List<WaterIntake>) {
        if (user == null) return
        this.waterIntake.updateWaterIntakes(waterIntakes.map { it.copy(updatedAt = Clock.System.now()
            .toEpochMilliseconds()) })
    }

    suspend fun deleteWaterIntakes(ids: List<Int>) {
        if (user == null) return
        this.waterIntake.deleteWaterIntakes(ids)
    }

    suspend fun deleteAllWaterIntakes() {
        if (user == null) return
        this.waterIntake.deleteAllWaterIntakes(user!!.uid)
    }

    suspend fun getWaterIntakesAfterTimeStamp(timeStamp: Long): List<WaterIntake> {
        if (user == null) return emptyList()
        return waterIntake.getWaterIntakesAfterTimeStamp(
            timeStamp,
            user!!.uid
        )
    }
}