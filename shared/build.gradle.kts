import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }


    
    listOf(
        // iosX64(),
        iosArm64(),
        // iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true

            // sharedViewModel
            export(libs.mvvm.core)
            export(libs.mvvm.flow)
        }
    }

    jvm()
    
    sourceSets {
        jvmMain.dependencies {
            //implementation(libs.firebase.auth.jvm)
            //implementation(libs.firebase.java.sdk)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.1.2"))
            //implementation(libs.firebase.analytics.ktx)
            //implementation(libs.firebase.auth.ktx)
            //implementation(libs.firebase.database.ktx)

            // sharedViewModel
            api(libs.mvvm.core)
            api(libs.mvvm.flow)
            api(libs.mvvm.flow.compose)


            implementation(libs.ktor.client.okhttp)
        }
        commonMain.dependencies {
            // sharedViewModel
            implementation(libs.mvvm.core)
            implementation(libs.mvvm.flow)
            implementation(libs.kotlinx.datetime)

            // Ktor dependencies
            implementation(libs.bundles.ktor)

            // new Firebase
            implementation("dev.gitlive:firebase-auth:1.13.0")
            implementation("dev.gitlive:firebase-firestore:1.13.0")
            implementation("dev.gitlive:firebase-storage:1.13.0")

            implementation ("com.google.android.libraries.places:places:3.3.0")

        }
        iosMain.dependencies {
            // sharedViewModel
            api(libs.mvvm.core)
            api(libs.mvvm.flow)

            implementation(libs.ktor.client.darwin)
        }
    }
}



android {
    namespace = "de.frederikkohler.bauchglueck.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
dependencies {
    //implementation(libs.androidx.lifecycle.livedata.core.ktx)
    //implementation(libs.firebase.firestore.ktx)
    //implementation(libs.firebase.storage.ktx)
    //implementation(libs.firebase.auth)
    implementation("dev.gitlive:firebase-auth:1.13.0")
    implementation("dev.gitlive:firebase-firestore:1.13.0")
    implementation("dev.gitlive:firebase-storage:1.13.0")
    implementation("dev.gitlive:firebase-analytics:1.13.0")
    implementation("dev.gitlive:firebase-database:1.13.0")
    implementation(libs.androidx.ui.text.android)
}
