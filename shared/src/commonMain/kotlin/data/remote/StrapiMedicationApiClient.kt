package data.remote

import data.local.entitiy.MedicationIntakeDataAfterTimeStamp
import data.network.createHttpClient
import data.remote.model.SyncResponse
import data.local.entitiy.IntakeStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import util.NetworkError
import util.Result

class StrapiMedicationApiClient(
    private val serverHost: String,
    private val httpClient: HttpClient = createHttpClient()
) {

    enum class ApiEndpoint(var urlPath: String, val method: HttpMethod) {
        MEDICATIONS_UPDATE_REMOTE_DATA("/api/medication/syncDeviceMedicationData", HttpMethod.Post),
        MEDICATIONS_FETCH_TIMERS_AFTER_TIMESTAMP("/api/medication/getUpdatedMedicationEntries?timeStamp={{timestamp}}&userId={{userID}}",
            HttpMethod.Get
        )
    }

    suspend fun updateRemoteData(entities: List<MedicationIntakeDataAfterTimeStamp>): Result<SyncResponse, NetworkError> {
        val endpoint = ApiEndpoint.MEDICATIONS_UPDATE_REMOTE_DATA

        val response = try {
            httpClient.post {
                url("${serverHost}${endpoint.urlPath}")
                contentType(ContentType.Application.Json)
                setBody(entities)
            }
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            logging().info { "StrapiClientException: ${e.message}" }
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        logging().info { "${serverHost}${endpoint.urlPath} -> ${response.status}" }

        return when (response.status.value) {
            in 200..299 -> {
                try {
                    Result.Success(response.body())
                } catch (e: SerializationException) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun fetchItemsAfterTimestamp(timestamp: Long, userID: String): Result<List<StrapiMedicationResponse>, NetworkError> {
        val endpoint = ApiEndpoint.MEDICATIONS_FETCH_TIMERS_AFTER_TIMESTAMP
        endpoint.urlPath = endpoint.urlPath.replace("{{timestamp}}", timestamp.toString()).replace("{{userID}}", userID)
        return apiCall(endpoint, timestamp)
    }

    private suspend inline fun <reified T> apiCall(endpoint: ApiEndpoint, body: Any? = null): Result<T, NetworkError> {
        logging().info { "Api Request: ${serverHost}${endpoint.urlPath}" }
        logging().info { "Current Data as TimeStamp: ${Clock.System.now().toEpochMilliseconds()}" }
        val response = try {
            when (endpoint.method) {
                HttpMethod.Get -> httpClient.get {
                    url("${serverHost}${endpoint.urlPath}")
                }
                HttpMethod.Post -> httpClient.post {
                    url("${serverHost}${endpoint.urlPath}")
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                HttpMethod.Put -> httpClient.put {
                    url("${serverHost}${endpoint.urlPath}")
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
                HttpMethod.Delete -> httpClient.delete {
                    url("${serverHost}${endpoint.urlPath}")
                }
                else -> throw UnsupportedOperationException("Unsupported HTTP method")
            }
        } catch (e: NoTransformationFoundException) {
            return Result.Error(NetworkError.NOTING_TO_SYNC)
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        } catch (e: Exception) {
            return Result.Error(NetworkError.REQUEST_TIMEOUT)
        }

        logging().info { "${serverHost}${endpoint.urlPath} -> ${response.status}" }

        return handleResult<T>(response)
    }

    private suspend inline fun <reified T> handleResult(response: HttpResponse): Result<T, NetworkError> {
        return when (response.status.value) {
            in 200..299 -> {
                try {
                    Result.Success(response.body())
                } catch (e: SerializationException) {
                    Result.Error(NetworkError.SERIALIZATION)
                }
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            430 -> Result.Error(NetworkError.NOTING_TO_SYNC)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}

@kotlinx.serialization.Serializable
data class StrapiMedicationResponse(
    @SerialName("id")
    val id: Int,

    @SerialName("userId")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("dosage")
    val dosage: String,

    @SerialName("isDeleted")
    val isDeleted: Boolean,

    @SerialName("medicationId")
    val medicationId: String,

    @SerialName("updatedAtOnDevice")
    val updatedAtOnDevice: Long,

    @SerialName("intake_times")
    val intakeTimes: List<IntakeTime>
) {
    fun toMedication(): data.local.entitiy.Medication {
        return data.local.entitiy.Medication(
            id = this.id,
            medicationId = this.medicationId,
            userId = this.userId,
            name = this.name,
            dosage = this.dosage,
            updatedAtOnDevice = this.updatedAtOnDevice,
            isDeleted = this.isDeleted
        )
    }
}

@kotlinx.serialization.Serializable
data class IntakeTime(
    @SerialName("id")
    val id: Int,

    @SerialName("intakeTime")
    val intakeTime: String,

    @SerialName("intakeTimeId")
    val intakeTimeId: String,

    @SerialName("updatedAtOnDevice")
    val updatedAtOnDevice: Long,

    @SerialName("intake_statuses")
    val intakeStatuses: List<IntakeStatus>
) {
    fun toIntakeTime(): data.local.entitiy.IntakeTime {
        return data.local.entitiy.IntakeTime(
            intakeTimeId = this.id.toString(),
            intakeTime = this.intakeTime,
            updatedAtOnDevice = this.updatedAtOnDevice
        )
    }
}

@kotlinx.serialization.Serializable
data class IntakeStatus(
    @SerialName("id")
    val id: Int,

    @SerialName("intakeStatusId")
    val intakeStatusId: String,

    @SerialName("date")
    val date: Long,

    @SerialName("isTaken")
    val isTaken: Boolean,

    @SerialName("updatedAtOnDevice")
    val updatedAtOnDevice: Long
) {
    fun toIntakeStatus(): data.local.entitiy.IntakeStatus {
        return data.local.entitiy.IntakeStatus(
            intakeStatusId = this.intakeStatusId,
            date = this.date,
            isTaken = this.isTaken,
            updatedAtOnDevice = this.updatedAtOnDevice
        )
    }
}