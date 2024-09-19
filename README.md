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
SharedDateTime: [Shared util.DateTime](https://raed-o-ghazal.medium.com/kotlinx-localdatetime-manipulation-for-kmm-eacfede93aba)
KMP Shared ROOM: [ROOM]("https://github.com/android/kotlin-multiplatform-samples/tree/main/Fruitties")
KMNotifier: [Tut](https://proandroiddev.com/how-to-implement-push-notification-in-kotlin-multiplatform-5006ff20f76c)
``` kotlin
1. -> ./gradlew :shared:build -> generates the server-<version>.jar for the server
```

## Configuration

To ensure the application runs correctly, you need to configure both the `API_KEY` and `API_HOST` in your `local.properties` file.

### Steps for Configuration:

1. **Obtain the API Key:**
  - Log in to your Strapi dashboard.
  - Navigate to the API Token settings and copy the required API Key.

2. **Set the API Key in `local.properties`:**
  - Open the `local.properties` file in your project directory.
  - Add or update the following entry:

    ```properties
    API_KEY=<YOUR_API_KEY>
    ```

  - Replace `<YOUR_API_KEY>` with the API Key you obtained from Strapi.

3. **Configure the API Host:**
  - In the same `local.properties` file, add or update the `API_HOST` entry to match the URL of your running Strapi instance:

    ```properties
    API_HOST=<YOUR_API_HOST>
    ```

  - Replace `<YOUR_API_HOST>` with the actual URL of your Strapi instance (e.g., `http://localhost:1337`).

4. **Save and Restart:**
  - Save the `local.properties` file.
  - Restart the application to apply the changes.

These configurations ensure that the application can correctly interact with your Strapi API.


## Backend (Strapi)
The backend for this project, built with Strapi, is available in the following repository:

Repository: https://github.com/ChromeSD22159/BauchGueckStrapiBackend
Please refer to the repository's README for instructions on how to set up and run the Strapi backend server.

Important: Make sure the `API_HOST` in your local.properties configuration in your Kotlin code matches the actual URL of your running Strapi instance.