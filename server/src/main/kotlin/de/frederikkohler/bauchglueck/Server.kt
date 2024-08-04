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

fun main(args: Array<String>) {
    // io.ktor.server.netty.EngineMain.main(args)
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    val env = ENV.Development

    configureDI(env)
    configureSerialization()
    configureDatabases()
    configureMonitoring()
    configureRouting()
}