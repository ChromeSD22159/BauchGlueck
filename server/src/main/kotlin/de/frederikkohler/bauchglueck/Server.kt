package de.frederikkohler.bauchglueck

import de.frederikkohler.bauchglueck.plugins.configureDI
import de.frederikkohler.bauchglueck.plugins.configureDatabases
import de.frederikkohler.bauchglueck.plugins.configureMonitoring
import de.frederikkohler.bauchglueck.plugins.configureRouting
import de.frederikkohler.bauchglueck.plugins.configureSerialization
import de.frederikkohler.bauchglueck.utils.ENV
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.engine.*

val env = ENV.Development

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            connector {
                port = 8080
            }
            module(Application::module)
            developmentMode = true
        }
    ).start(wait = true)
}

fun Application.module() {
    configureDI(env)
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureRouting()
}