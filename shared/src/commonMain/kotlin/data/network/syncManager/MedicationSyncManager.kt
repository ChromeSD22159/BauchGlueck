package data.network.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.MedicationDao
import data.local.dao.SyncHistoryDao
import data.local.entitiy.IntakeStatus
import data.local.entitiy.IntakeTime
import data.local.entitiy.Medication
import data.local.entitiy.MedicationIntakeDataAfterTimeStamp
import data.local.entitiy.SyncHistory
import data.remote.StrapiMedicationApiClient
import data.remote.StrapiMedicationResponse
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

        /*
        // 3. Vom Server alle seit dem letzten Sync geänderten Timer abrufen
        val response = apiService.fetchItemsAfterTimestamp(lastSync, user!!.uid)
        response.onSuccess { serverTimers ->

            logging().info { "serverTimers: ${serverTimers.size}" }
            logging().info { "localTimers: ${localChangedMedications.size}" }

            logging().info { "Received Timer to Update from Server < < <" }
            serverTimers.forEach {
                logging().info { "< < < Timer: ${it.toString()}" }
            }

            for (serverMedication in serverTimers) {
                val localMedication = localService.getMedicationsWithIntakeTimesByMedicationID(serverMedication.medicationId)

                if (localMedication != null) {
                    logging().info { "Medication: ${serverMedication.name}: ${serverMedication.updatedAtOnDevice}" }

                    if (serverMedication.updatedAtOnDevice > localMedication.medication.updatedAtOnDevice) {
                        // Wenn der medication auf dem Server neuer ist, aktualisiere den lokalen Timer
                        localMedication.medication.name = serverMedication.name
                        localMedication.medication.isDeleted = serverMedication.isDeleted
                        localMedication.medication.updatedAtOnDevice = serverMedication.updatedAtOnDevice
                        localMedication.medication.dosage = serverMedication.dosage

                        localService.insertMedication(localMedication.medication)
                        localService.insertIntakeTimes(localMedication.intakeTimesWithStatus.map { it.intakeTime })
                        localService.insertIntakeStatuses(localMedication.intakeTimesWithStatus.flatMap { it.intakeStatuses })
                    }
                } else {
                    // Wenn der Medication noch nicht lokal existiert, füge ihn hinzu
                    localService.insertMedication(
                        Medication(
                            id = serverMedication.id,
                            medicationId = serverMedication.medicationId,
                            userId = serverMedication.userId,
                            name = serverMedication.name,
                            dosage = serverMedication.dosage,
                            updatedAtOnDevice = serverMedication.updatedAtOnDevice,
                            isDeleted = serverMedication.isDeleted
                        )
                    )

                    localService.insertIntakeTimes(
                        serverMedication.intakeTimes.map {
                            IntakeTime(
                                intakeTimeId = it.id.toString(),
                                intakeTime = it.intakeTime,
                                medicationId = serverMedication.medicationId,
                                updatedAtOnDevice = it.updatedAtOnDevice
                            )
                        }
                    )

                    serverMedication.intakeTimes.map {
                        localService.insertIntakeStatuses(
                            it.intakeStatuses.map { intakeStatus ->
                                IntakeStatus(
                                    intakeTimeId = it.id.toString(),
                                    date = intakeStatus.date.toLong(),
                                    isTaken = intakeStatus.isTaken,
                                    updatedAtOnDevice = intakeStatus.updatedAtOnDevice
                                )
                            }
                        )
                    }
                }
            }

            val newTimerStamp = SyncHistory(deviceId = deviceID,table = table)
            syncHistory.insertSyncHistory(newTimerStamp)

            logging().info { "Save TimerStamp: ${newTimerStamp.lastSync}" }
        }
        response.onError { return }
        */
    }
}