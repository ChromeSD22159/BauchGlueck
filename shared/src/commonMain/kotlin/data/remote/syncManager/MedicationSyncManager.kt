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
import data.remote.StrapiMedicationApiClient
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import util.debugJsonHelper
import util.onError
import util.onSuccess

class MedicationSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.MEDICATION,
    private var user: FirebaseUser? = Firebase.auth.currentUser
) {
    private val apiService: StrapiMedicationApiClient = StrapiMedicationApiClient(serverHost)
    private var localService: MedicationDao = LocalDataSource(db).medications
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    private suspend fun sendChangedEntriesToServer(items: List<MedicationIntakeDataAfterTimeStamp>) {
        withContext(Dispatchers.IO) {
            try {
                logging().info { "Send Medication to Update on Server > > >" }
                items.forEach {
                    logging().info { "> > > Medication: $it" }
                }

                val response = apiService.updateRemoteData(items)

                logging().info { "Send Medication to Update on Server < < < $response" }

                // TODO: Handle Response - - Maybe Hard delete Medication? and send ids to server?
                response.onSuccess { logging().info { "Medication Sync Success" } }
                response.onError { logging().info { "Medication Sync Error" } }
            } catch (e: Exception) {
                logging().info { "Medication Sync Error" }
            }
        }
    }

    suspend fun syncMedications() {
        if (user == null) return

        val lastSync = syncHistory.getLatestSyncTimer(deviceID).sortedByDescending { it.lastSync }.firstOrNull { it.table == table }?.lastSync ?: 0L
        val localChangedMedications = localService.getMedicationsWithIntakeTimesAfterTimeStamp(lastSync, user!!.uid)

        logging().info { "* * * * * * * * * * SYNCING * * * * * * * * * * " }
        logging().info { "Last Sync Success: $lastSync" }

        localChangedMedications.forEach { item ->
            logging().info { ">>> Medication: ${item.medication.name} ${item.medication.updatedAtOnDevice}" }

            val timesCount = item.intakeTimesWithStatus.map { it.intakeTime }.count()
            logging().info { ">>> TimesCount: $timesCount" }
            item.intakeTimesWithStatus.map {
                val intakeStatusCount = it.intakeStatuses.count()
                logging().info { ">>> TimesIntakesCount: $intakeStatusCount" }
            }
        }

        sendChangedEntriesToServer(localChangedMedications)

        val response = apiService.fetchItemsAfterTimestamp(lastSync, user!!.uid)
        response.onSuccess { serverTimers ->

            logging().info { "serverTimers: ${serverTimers.size}" }
            logging().info { "localTimers: ${localChangedMedications.size}" }

            logging().info { "Received Timer to Update from Server < < <" }
            serverTimers.forEach {
                logging().info { "< < < Timer: ${it.toString()}" }
            }

            for (serverMedication in serverTimers) {

                val existingMedicationOrNull = localService.getMedicationByMedicationId(serverMedication.medicationId)
                val medicationToUpdate = serverMedication.toMedication()

                logging().info { "existingMedicationOrNull: ${existingMedicationOrNull?.medicationId}" }
                logging().info { "medicationToUpdate: ${medicationToUpdate.medicationId}" }

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

                //debugJsonHelper(intakeStatuses)

                localService.insertIntakeStatuses(intakeStatuses)
            }

            val newTimerStamp = SyncHistory(deviceId = deviceID,table = table)
            syncHistory.insertSyncHistory(newTimerStamp)

            logging().info { "Save TimerStamp: ${newTimerStamp.lastSync}" }
        }
        response.onError { return }

    }
}