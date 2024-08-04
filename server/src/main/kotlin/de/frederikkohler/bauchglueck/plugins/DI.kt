package de.frederikkohler.bauchglueck.plugins


import de.frederikkohler.bauchglueck.services.IngredientFormsDatabaseService
import de.frederikkohler.bauchglueck.services.MeasurementUnitsDatabaseService
import de.frederikkohler.bauchglueck.utils.ENV
import de.frederikkohler.bauchglueck.utils.EnvService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI(dotenv: ENV){
    val appModule= module {
        single<EnvService> { EnvService(dotenv) }

        single { IngredientFormsDatabaseService() }
        single { MeasurementUnitsDatabaseService() }
    }

    install(Koin){
        modules(appModule)
    }
}