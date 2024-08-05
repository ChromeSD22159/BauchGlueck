import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
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
        androidMain.dependencies {
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.1.2"))
            implementation(libs.firebase.analytics.ktx)
            implementation(libs.firebase.auth.ktx)
            implementation(libs.firebase.database.ktx)

            // sharedViewModel
            api(libs.mvvm.core)
            api(libs.mvvm.flow)
            api(libs.mvvm.flow.compose)

            // dataManager
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            // sharedViewModel
            implementation(libs.mvvm.core)
            implementation(libs.mvvm.flow)
            implementation(libs.kotlinx.datetime)

            // Ktor dependencies
            implementation(libs.ktor.client.core)


            // Other Ktor features as needed
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
        }
        iosMain.dependencies {
            // sharedViewModel
            api(libs.mvvm.core)
            api(libs.mvvm.flow)

            // dataManager
            implementation(libs.ktor.client.ios)
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
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.work.runtime.ktx)
}

/*
api: Macht Abhängigkeiten für andere Module sowohl zur Kompilierungs- als auch zur Laufzeit sichtbar.
implementation: Macht Abhängigkeiten für andere Module nur zur Laufzeit sichtbar.
Wahl der Konfiguration: Wähle api, wenn du eine Bibliothek entwickelst und die APIs der Abhängigkeiten Teil deiner öffentlichen API sein sollen. Wähle implementation für interne Abhängigkeiten, die nicht von anderen Modulen direkt verwendet werden sollen.
 */