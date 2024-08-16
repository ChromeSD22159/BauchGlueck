import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }

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

            linkerOpts.add("-lsqlite3")   // Required when using NativeSQLiteDriver
        }
    }

    jvm()
    
    sourceSets {
        jvmMain.dependencies {

        }
        androidMain.dependencies {
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.1.2"))

            // sharedViewModel
            api(libs.mvvm.core)
            api(libs.mvvm.flow)
            api(libs.mvvm.flow.compose)


            implementation(libs.ktor.client.okhttp)

            implementation(libs.room.runtime.android)
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

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

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
    implementation(libs.bundles.firebase.services)
    implementation(libs.androidx.ui.text.android)

    // Room
    add("kspCommonMainMetadata", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata" ) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}