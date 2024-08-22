package data.repositories

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.dao.WeightDao
import data.local.entitiy.Weight
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.datetime.Clock

class WeightRepository(
    db: LocalDatabase,
    private var user: FirebaseUser? = Firebase.auth.currentUser
) {
    private var weight: WeightDao = LocalDataSource(db).weight

    suspend fun getAllWeights(): List<Weight> {
        return if (user == null) return emptyList()
        else weight.getAllWeights(user!!.uid)
    }

    suspend fun getWeightById(id: Int): Weight? = this.weight.getWeightById(id)

    suspend fun insertOrUpdateWeight(weight: Weight) = this.weight.insertOrUpdateWeight(weight)

    suspend fun insertOrUpdateWeights(weights: List<Weight>) {
        weights.forEach {
            this.weight.insertOrUpdateWeight(it.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
        }
    }

    suspend fun updateWeights(weights: List<Weight>) {
        if (user == null) return
        this.weight.updateWeights(weights)
    }

    suspend fun deleteWeights(ids: List<Int>) {
        if (user == null) return
        this.weight.deleteWeight(ids)
    }

    suspend fun deleteAllWeights() {
        if (user == null) return
        this.weight.deleteAllWeights(user!!.uid)
    }

    suspend fun getWeightsAfterTimeStamp(timeStamp: Long): List<Weight> {
        if (user == null) return emptyList()
        return this.weight.getWeightsAfterTimeStamp(
            timeStamp,
            user!!.uid
        )
    }
}