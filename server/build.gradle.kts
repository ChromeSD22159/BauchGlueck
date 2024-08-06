plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "de.frederikkohler.bauchglueck"
version = "1.0.0"
application {
    mainClass.set("de.frederikkohler.bauchglueck.ServerKt")
    project.setProperty("mainClassName", mainClass.get())
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

repositories {
    mavenCentral()
}

ktor {
    fatJar {
        archiveFileName.set("server.jar")
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.koin)
    implementation(libs.koin.core)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.h2database)
    implementation(libs.cdimascio.dotenv) // ENV Loader
    implementation(libs.logback.classic)
    implementation(libs.mysql.connector.java) // DB CONNECTOR
    implementation(libs.hikaricp) // DB DRIVER
    implementation(libs.flyway.core) // Version control for your database
}
