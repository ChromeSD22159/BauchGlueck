package de.frederikkohler.bauchglueck.plugins


import de.frederikkohler.bauchglueck.utils.ENV
import de.frederikkohler.bauchglueck.utils.EnvService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI(dotenv: ENV){
    val appModule= module {
        single<EnvService> { EnvService(dotenv) }
    }

    install(Koin){
        modules(appModule)
    }
}