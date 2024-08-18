package data.remote

class RemoteDataSource(
    private var serverHost: String,
) {
    var countdownTimer = StrapiCountdownTimerApiClient(serverHost)
}