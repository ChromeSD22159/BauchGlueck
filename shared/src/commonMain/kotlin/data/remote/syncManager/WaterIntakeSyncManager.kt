package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.SyncHistoryDao
import data.local.dao.WaterIntakeDao
import data.local.entitiy.WaterIntake
import data.remote.BaseApiClient
import data.remote.StrapiApiClient
import data.remote.model.SyncResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import org.lighthousegames.logging.logging
import util.onError
import util.onSuccess

class WaterIntakeSyncManager(
    db: LocalDatabase,
    serverHost: String,
    var deviceID: String,
    private val table: RoomTable = RoomTable.WATER_INTAKE,
    private var user: FirebaseUser? = Firebase.auth.currentUser
): BaseSyncManager() {
    private val apiService: StrapiApiClient = StrapiApiClient(serverHost)
    private var localService: WaterIntakeDao = LocalDataSource(db).waterIntake
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncWaterIntakes() {
        if (user == null) return

        val lastSync = syncHistory.lastSync(deviceID, table)

        val localChangedWeights = localService.getAllAfterTimeStamp(lastSync, user!!.uid)

        apiService.sendChangedEntriesToServer<WaterIntake, SyncResponse>(
            localChangedWeights,
            table,
            BaseApiClient.UpdateRemoteEndpoint.WATER_INTAKE
        )

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
