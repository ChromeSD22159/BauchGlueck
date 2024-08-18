package data

import data.local.LocalDataSource
import data.local.LocalDataSourceImpl
import data.local.LocalDatabase
import data.local.entitiy.CountdownTimer
import data.remote.RemoteDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import util.onSuccess

class Repository(
    private val db: LocalDatabase,
    private val serverHost: String = "192.168.1.57:1337"
) {
    private var localDataSource: LocalDataSource = LocalDataSourceImpl(db)
    private var remoteDataSource: RemoteDataSource = RemoteDataSource(serverHost)
    private var firebase: Firebase = Firebase

    private val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoadingState: StateFlow<Boolean>
        get() = isLoading


    suspend fun getTimer() {
        val currentUser: FirebaseUser? = firebase.auth.currentUser

        val remoteData = remoteDataSource.countdownTimer.getCountdownTimers()
        remoteData.onSuccess { timerResponse ->

            val timer = timerResponse.data

            timer.forEach {

                val timerConverted = CountdownTimer(
                    timerId = it.id.toString(),
                    userId = it.attributes.userId,
                    name = it.attributes.name,
                    duration = it.attributes.duration.toLong(),
                    startDate = it.attributes.startDate?.toLong(),
                    endDate = it.attributes.endDate?.toLong(),
                    timerState = it.attributes.timerState,
                    showActivity = it.attributes.showActivity ?: true,
                    createdAt = it.attributes.createdAt.toLong(),
                    updatedAt = it.attributes.updatedAt.toLong()
                )

                localDataSource.insertTimer(timerConverted)
            }

        }

    }

}