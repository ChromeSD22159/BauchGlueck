package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.SyncHistoryDao
import data.local.dao.WaterIntakeDao
import data.local.entitiy.MedicationIntakeDataAfterTimeStamp
import data.local.entitiy.SyncHistory
import data.local.entitiy.WaterIntake
import data.remote.BaseApiClient
import data.remote.StrapiApiClient
import data.remote.model.SyncResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import util.onError
import util.onSuccess

class WaterIntakeSyncManager(
    db: LocalDatabase,
    serverHost: String,
    var deviceID: String,
    private val table: RoomTable = RoomTable.WATER_INTAKE,
    private var user: FirebaseUser? = Firebase.auth.currentUser
) {
    private val apiService: StrapiApiClient = StrapiApiClient(serverHost)
    private var localService: WaterIntakeDao = LocalDataSource(db).waterIntake
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncWaterIntakes() {
        if (user == null) return

        val lastSync = syncHistory.lastSync(deviceID, table)

        val localChangedWeights = localService.getAllAfterTimeStamp(lastSync, user!!.uid)

        apiService.sendChangedEntriesToServer<WaterIntake, SyncResponse>(localChangedWeights)

        logging().info { "* * * * * * * * * * SYNCING * * * * * * * * * * " }
        logging().info { "Last Sync Success: $lastSync" }

        val response = apiService.fetchItemsAfterTimestamp<List<WaterIntake>>(
            BaseApiClient.FetchAfterTimestampEndpoint.WATER_INTAKE,
            lastSync,
            user!!.uid
        )

        response.onSuccess {serverWeights ->
            for (serverWeight in serverWeights) {

                val localWeight = localService.getById(serverWeight.waterIntakeId)

                if (localWeight != null) {
                    // Wenn der WaterIntake auf dem Server neuer ist, aktualisiere den lokalen Weight
                    if (serverWeight.updatedAtOnDevice > localWeight.updatedAtOnDevice) {
                        localWeight.value = serverWeight.value
                        localWeight.updatedAtOnDevice = serverWeight.updatedAtOnDevice
                        localWeight.isDeleted = serverWeight.isDeleted
                        localService.insertOrUpdate(localWeight)
                    }
                } else {
                    // Wenn der Weight noch nicht lokal existiert, füge ihn hinzu
                    localService.insertOrUpdate(
                        WaterIntake(
                            waterIntakeId = serverWeight.waterIntakeId,
                            userId = serverWeight.userId,
                            value = serverWeight.value,
                            updatedAtOnDevice = serverWeight.updatedAtOnDevice,
                            isDeleted = serverWeight.isDeleted,
                        )
                    )
                }

                val newWeightStamp = syncHistory.setNewTimeStamp(table, deviceID)
                logging().info { "Save WeightStamp: ${newWeightStamp.lastSync}" }
                val localWeights = localService.getAll(user!!.uid)
                logging().info { "Save WeightStamp after Sync: ${localWeights.size}" }
            }
        }
        response.onError {
            logging().info { "Weight Sync Error: ${it.name}" }
            val localWeights = localService.getAll(user!!.uid)
            logging().info { "Save WeightStamp after Sync: ${localWeights.size}" }
            return
        }
    }

}

suspend inline fun <T: SyncHistoryDao> T.lastSync(deviceID: String, table: RoomTable): Long {
    return this.getLatestSyncTimer(deviceID).sortedByDescending { it.lastSync }.firstOrNull { it.table == table }?.lastSync ?: 0L
}

suspend inline fun <T: SyncHistoryDao> T.setNewTimeStamp(table: RoomTable, deviceID: String): SyncHistory {
    val newWeightStamp = SyncHistory(deviceId = deviceID, table = table)
    this.insertSyncHistory(newWeightStamp)
    return newWeightStamp
}

suspend inline fun <Q, reified R> StrapiApiClient.sendChangedEntriesToServer(items: List<Q>) {
    if (items.isEmpty()) {
        logging().info { "Nothing to Send > > >" }
        return
    }

    val client = this
    withContext(Dispatchers.IO) {
        try {
            logging().info { "Send Weight to Update on Server > > >" }
            items.forEach {
                logging().info { "> > > Weight: ${it.toString()}" }
            }

            val response = client.updateRemoteData<List<Q>, R>(
                BaseApiClient.UpdateRemoteEndpoint.MEDICATION,
                items
            )

            // TODO: Handle Response - - Maybe Hard delete Weight? and send ids to server?
            response.onSuccess { logging().info { "Weight Sync Success" } }
            response.onError { logging().info { "Weight Sync Error" } }
        } catch (e: Exception) {
            logging().info { "Weight Sync Error" }
        }
    }
}