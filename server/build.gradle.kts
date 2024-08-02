plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "de.frederikkohler.bauchglueck"
version = "1.0.0"
application {
    mainClass.set("de.frederikkohler.bauchglueck.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio.jvm)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.koin)
    implementation(libs.koin.core)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.h2database)
    implementation(libs.cdimascio.dotenv) // ENV Loader

    implementation("mysql:mysql-connector-java:8.0.28") // DB CONNECTOR
    implementation("com.zaxxer:HikariCP:5.1.0") // DB DRIVER
    implementation("org.flywaydb:flyway-core:9.16.0") // Version control for your database
}