package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.SyncHistoryDao
import data.local.dao.WeightDao
import data.local.entitiy.SyncHistory
import data.local.entitiy.WaterIntake
import data.local.entitiy.Weight
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

class WeightSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.WEIGHT,
    private var user: FirebaseUser? = Firebase.auth.currentUser
): BaseSyncManager() {
    private val apiService: StrapiApiClient = StrapiApiClient()
    private var localService: WeightDao = LocalDataSource(db).weight
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncWeights() {
        if (user == null) return

        val lastSync = syncHistory.lastSync(deviceID, table)
        val localChangedWeights = localService.getAllAfterTimeStamp(lastSync, user!!.uid)

        apiService.sendChangedEntriesToServer<Weight, SyncResponse>(
            localChangedWeights,
            table,
            BaseApiClient.UpdateRemoteEndpoint.WEIGHT
        )

        logging().info { "* * * * * * * * * * SYNCING * * * * * * * * * * " }
        logging().info { "Last Sync Success: $lastSync" }

        // 3. Vom Server alle seit dem letzten Sync geänderten Weight abrufen
        val response = apiService.fetchItemsAfterTimestamp<List<Weight>>(
            BaseApiClient.FetchAfterTimestampEndpoint.WEIGHT,
            lastSync,
            user!!.uid
        )

        response.onSuccess { serverWeights ->

            logging().info { "serverWeights: ${serverWeights.size}" }
            logging().info { "localWeights: ${localChangedWeights.size}" }

            logging().info { "Received Weight to Update from Server < < <" }
            serverWeights.forEach {
                logging().info { "< < < Weight: ${it.toString()}" }
            }

            for (serverWeight in serverWeights) {
                val localWeight = localService.getById(serverWeight.weightId)

                if (localWeight != null) {
                    logging().info { "Weight: ${serverWeight.value}: ${serverWeight.updatedAtOnDevice}" }
                    logging().info { "Weight: ${localWeight.value}: ${localWeight.updatedAtOnDevice}" }
                    if (serverWeight.updatedAtOnDevice > localWeight.updatedAtOnDevice) {
                        // Wenn der Weight auf dem Server neuer ist, aktualisiere den lokalen Weight
                        localWeight.value = serverWeight.value
                        localWeight.updatedAtOnDevice = serverWeight.updatedAtOnDevice
                        localWeight.isDeleted = serverWeight.isDeleted
                        localWeight.weighed = serverWeight.weighed
                        localWeight.updatedAt = serverWeight.updatedAt

                        localService.insert(localWeight)
                    }
                } else {
                    // Wenn der Weight noch nicht lokal existiert, füge ihn hinzu
                    localService.insert(
                        Weight(
                            weightId = serverWeight.weightId,
                            userId = serverWeight.userId,
                            value = serverWeight.value,
                            weighed = serverWeight.weighed,
                            updatedAtOnDevice = serverWeight.updatedAtOnDevice,
                            isDeleted = serverWeight.isDeleted,
                            updatedAt = serverWeight.updatedAt
                        )
                    )
                }
            }

            val newWeightStamp = syncHistory.setNewTimeStamp(table, deviceID)
            logging().info { "Save WeightStamp: ${newWeightStamp.lastSync}" }
            val localWeights = localService.getAll(user!!.uid)
            logging().info { "Save WeightStamp after Sync: ${localWeights.size}" }

        }
        response.onError {
            logging().info { "Weight Sync Error: ${it.name}" }
            val localWeights = localService.getAll(user!!.uid)
            logging().info { "Save WeightStamp after Sync: ${localWeights.size}" }
            return
        }
    }
}