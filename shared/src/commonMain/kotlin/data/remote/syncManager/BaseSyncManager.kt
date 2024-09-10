package data.remote.syncManager

import data.local.RoomTable
import data.local.dao.SyncHistoryDao
import data.local.entitiy.SyncHistory
import data.remote.BaseApiClient
import data.remote.StrapiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import util.onError
import util.onSuccess

open class BaseSyncManager {
    suspend inline fun <T: SyncHistoryDao> T.lastSync(deviceID: String, table: RoomTable): Long {
        return this.getLatestSyncTimer(deviceID).sortedByDescending { it.lastSync }.firstOrNull { it.table == table }?.lastSync ?: 0L
    }

    suspend inline fun <T: SyncHistoryDao> T.setNewTimeStamp(table: RoomTable, deviceID: String): SyncHistory {
        val newWeightStamp = SyncHistory(deviceId = deviceID, table = table)
        this.insertSyncHistory(newWeightStamp)
        return newWeightStamp
    }

    suspend inline fun <reified Q: Any, reified R: Any> StrapiApiClient.sendChangedEntriesToServer(items: List<Q>, table: RoomTable, apiEndpoint: BaseApiClient. UpdateRemoteEndpoint) {
        if (items.isEmpty()) {
            logging().info { "Nothing to Send > > >" }
            return
        }

        val client = this
        withContext(Dispatchers.IO) {
            try {
                logging().info { "Send ${table.tableName} to Update on Server > > >" }
                items.forEach {
                    logging().info { "> > > ${table.tableName}: ${it.toString()}" }
                }

                val response = client.updateRemoteData<List<Q>, R>(
                    apiEndpoint,
                    items
                )

                // TODO: Handle Response - - Maybe Hard delete Weight? and send ids to server?
                response.onSuccess { logging().info { "${table.tableName} Sync Success" } }
                response.onError { logging().info { "${table.tableName} Sync Error $it" } }
            } catch (e: Exception) {
                logging().info { "${table.tableName} Sync Error" }
            }
        }
    }
}