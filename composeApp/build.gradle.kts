import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    id("dev.sergiobelda.compose.vectorize") version "1.0.0"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation("androidx.compose.animation:animation:1.7.2")
            implementation("androidx.core:core-splashscreen:1.0.1")
            implementation("com.google.android.gms:play-services-ads:22.6.0")
            implementation("com.google.android.gms:play-services-base:18.5.0")
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.multiplatform.settings)
            api(libs.logging)
            api(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.mp)
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.compose.core)

            implementation(libs.kmpauth.google)
            implementation(libs.kmpauth.firebase)
            implementation(libs.kmpauth.uihelper)
        }
    }
}

android {
    namespace = "de.frederikkohler.bauchglueck"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "de.frederikkohler.bauchglueck"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 8
        versionName = "1.0.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    dependencies {
        debugImplementation(compose.uiTooling)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
}

apply(plugin = "com.google.gms.google-services")

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.text.google.fonts)
    //implementation(libs.play.services.measurement.api)
    implementation(libs.bundles.firebase.services)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.compose.vectorize.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.koin.androidx.compose)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.0")
    implementation(libs.androidx.lifecycle.process)
}

