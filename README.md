This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.


SharedViewModel: [Github Shared MVVM](https://github.com/sunildhiman90/KMPAppSharedViewModel/tree/main)
SharedDateTime: [Shared DateTime](https://raed-o-ghazal.medium.com/kotlinx-localdatetime-manipulation-for-kmm-eacfede93aba)
KMP Shared ROOM: [ROOM]("https://github.com/android/kotlin-multiplatform-samples/tree/main/Fruitties")

``` kotlin
1. -> ./gradlew :shared:build -> generates the server-<version>.jar for the server
```

### Server configuration
The server URL for this project is managed via the ServerHost enum, which is located in `Shared/commonMain/kotlin/data/network/ServerHost`.
``` kotlin
enum class ServerHost(val url: String) {
    LOCAL_FREDERIK("http://192.168.0.73:1337"),
    LOCAL_SABINA("http://192.168.1.57:1337"),
    PRODUCTION("https://api.frederikkohler.de/bauchglueck")
}
```

### Server URL assignment
The current server URL that the application connects to is set in the file `Shared/commonMain/kotlin/di/repositoriesModule.kt`.
``` kotlin
val serverHost = ServerHost.LOCAL_SABINA.url
```
`Hinweis: Ändern Sie den Wert von serverHost in repositoriesModule.kt, um zwischen den verschiedenen Serverumgebungen zu wechseln (z. B. lokal, Entwicklung, Produktion).`

### Backend (Strapi)
The backend for this project, built with Strapi, is available in the following repository:

Repository: https://github.com/ChromeSD22159/BauchGueckStrapiBackend
Please refer to the repository's README for instructions on how to set up and run the Strapi backend server.

Important: Make sure the serverHost configuration in your Kotlin code matches the actual URL of your running Strapi instance.