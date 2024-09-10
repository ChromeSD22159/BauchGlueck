package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.MedicationDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.IntakeStatus
import data.local.entitiy.IntakeTime
import data.local.entitiy.MedicationIntakeDataAfterTimeStamp
import data.local.entitiy.SyncHistory
import data.local.entitiy.WaterIntake
import data.remote.BaseApiClient
import data.remote.StrapiApiClient
import data.remote.model.ApiMedicationResponse
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

class MedicationSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.MEDICATION,
    private var user: FirebaseUser? = Firebase.auth.currentUser
): BaseSyncManager() {
    private val apiService: StrapiApiClient = StrapiApiClient(serverHost)
    private var localService: MedicationDao = LocalDataSource(db).medications
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncMedications() {
        if (user == null) return

        val lastSync = syncHistory.lastSync(deviceID, table)

        val localChangedMedications = localService.getMedicationsWithIntakeTimesAfterTimeStamp(lastSync, user!!.uid)

        apiService.sendChangedEntriesToServer<MedicationIntakeDataAfterTimeStamp, SyncResponse>(
            localChangedMedications,
            table,
            BaseApiClient.UpdateRemoteEndpoint.MEDICATION
        )

        val response = apiService.fetchItemsAfterTimestamp<List<ApiMedicationResponse>>(
            BaseApiClient.FetchAfterTimestampEndpoint.MEDICATION,
            lastSync,
            user!!.uid
        )

        response.onSuccess { serverTimers ->
            logging().info { "serverTimers: ${serverTimers.size}" }
            logging().info { "localTimers: ${localChangedMedications.size}" }

            logging().info { "Received Timer to Update from Server < < <" }
            serverTimers.forEach {
                logging().info { "< < < Timer: ${it.toString()}" }
            }

            for (serverMedication in serverTimers) {
                val medicationToUpdate = serverMedication.toMedication()

                localService.insertMedication(medicationToUpdate)

                val intakeTimes = serverMedication.intakeTimes.map { intakeTime ->
                    IntakeTime(
                        intakeTimeId = intakeTime.intakeTimeId,
                        intakeTime = intakeTime.intakeTime,
                        updatedAtOnDevice = intakeTime.updatedAtOnDevice,
                        isDeleted = false,
                        medicationId = medicationToUpdate.medicationId
                    )
                }.filter { it.intakeTimeId.length >= 19 }
                localService.insertIntakeTimes(intakeTimes)


                val intakeStatuses: List<IntakeStatus> = serverMedication.intakeTimes.flatMap { intakeTime ->
                    intakeTime.intakeStatuses.map { status ->
                        IntakeStatus(
                            intakeStatusId = status.intakeStatusId,
                            intakeTimeId = intakeTime.intakeTimeId,
                            date = status.date,
                            isTaken = status.isTaken,
                            isDeleted = status.isDeleted
                        )
                    }
                }.filter { it.intakeStatusId.length >= 19 && it.intakeTimeId.length >= 19 }

                localService.insertIntakeStatuses(intakeStatuses)
            }

            syncHistory.setNewTimeStamp(table, deviceID)
        }
        response.onError { return }

    }
}