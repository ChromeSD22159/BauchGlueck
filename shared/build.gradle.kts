import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    //alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    id("com.codingfeline.buildkonfig") version "0.15.2"
}

kotlin {

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
        kotlin.srcDir("build/generated/buildkonfig")
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
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

            linkerOpts.add("-lsqlite3")
            export(libs.kmpnotifier)
        }
    }

    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.1.2"))

            // sharedViewModel
            api(libs.mvvm.core)
            api(libs.mvvm.flow)
            api(libs.mvvm.flow.compose)


            implementation(libs.ktor.client.okhttp)

            implementation(libs.room.runtime.android)

            implementation(libs.koin.android)

            implementation(libs.play.services.auth)
        }
        commonMain.dependencies {
            // sharedViewModel
            implementation(libs.mvvm.core)
            implementation(libs.mvvm.flow)
            implementation(libs.kotlinx.datetime)

            // Ktor dependencies
            implementation(libs.bundles.ktor)

            // new Firebase
            implementation(libs.bundles.firebase.services)
            implementation(libs.firebase.gitlive.common)
            implementation(libs.room.common)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            implementation(libs.multiplatform.settings)


            implementation("io.github.mirzemehdi:kmpauth-google:2.1.0-alpha02")
            implementation("io.github.mirzemehdi:kmpauth-firebase:2.1.0-alpha02")
            implementation("io.github.mirzemehdi:kmpauth-uihelper:2.1.0-alpha02")

            implementation(libs.koin.core)
            api(libs.logging)
            api(libs.kmpnotifier)

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

buildkonfig {
    packageName = "de.frederikkohler.bauchglueck.shared"

    defaultConfigs {
        val api: String = gradleLocalProperties(rootDir).getProperty("API")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API", api)

        val apiHost: String = gradleLocalProperties(rootDir).getProperty("API_HOST") ?: ""
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_HOST", apiHost)

        val isDEV: String = gradleLocalProperties(rootDir).getProperty("DEV") ?: ""
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "DEV", isDEV)

        val appStartAdId: String = gradleLocalProperties(rootDir).getProperty("APP_START_AD_ID")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "APP_START_AD_ID", appStartAdId)
    }
}

dependencies {
    //implementation(projects.composeApp)
    implementation(libs.bundles.firebase.services)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.androidx.core.i18n)
    implementation("co.touchlab:stately-common:1.2.1")
    // Room
    add("kspCommonMainMetadata", libs.room.compiler)

    //add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata" ) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}