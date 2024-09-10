package data.remote.syncManager

import data.local.LocalDataSource
import data.local.LocalDatabase
import data.local.RoomTable
import data.local.dao.MealPlanDao
import data.local.dao.MedicationDao
import data.local.dao.SyncHistoryDao
import data.remote.BaseApiClient

import data.remote.StrapiApiClient
import data.remote.model.ApiMealPlanDayResponse
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import util.debugJsonHelper
import util.onSuccess

class MealPlanSyncManager(
    db: LocalDatabase,
    serverHost: String,
    private var deviceID: String,
    private val table: RoomTable = RoomTable.MEAL_PLAN,
    private var user: FirebaseUser? = Firebase.auth.currentUser
): BaseSyncManager() {
    private val apiService: StrapiApiClient = StrapiApiClient(serverHost)
    private var localService: MealPlanDao = LocalDataSource(db).mealPlan
    private var syncHistory: SyncHistoryDao = LocalDataSource(db).syncHistory

    suspend fun syncMealPlan() {
        if (user == null) return
        val lastSync = syncHistory.getLatestSyncTimer(deviceID).sortedByDescending { it.lastSync }.firstOrNull { it.table == table }?.lastSync ?: 0L

        val response = apiService.fetchItemsAfterTimestamp<List<ApiMealPlanDayResponse>>(
            BaseApiClient.FetchAfterTimestampEndpoint.MealPlan,
            lastSync,
            user!!.uid
        )

        response.onSuccess {
            debugJsonHelper(it)
        }

        /*
        apiService.sendChangedEntriesToServer<MedicationIntakeDataAfterTimeStamp, SyncResponse>(
            localChangedMedications,
            table,
            BaseApiClient.UpdateRemoteEndpoint.MEDICATION
        )
        */

        /*
        val response = apiService.fetchItemsAfterTimestamp<List<ApiMedicationResponse>>(
            BaseApiClient.FetchAfterTimestampEndpoint.MEDICATION,
            lastSync,
            user!!.uid
        )

        response.onSuccess { serverTimers ->

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
        */
    }
}