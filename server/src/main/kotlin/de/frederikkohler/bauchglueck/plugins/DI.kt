package de.frederikkohler.bauchglueck.plugins


import de.frederikkohler.bauchglueck.services.IngredientFormsDatabaseService
import de.frederikkohler.bauchglueck.services.MeasurementUnitsDatabaseService
import de.frederikkohler.bauchglueck.services.RecipeCategoryDatabaseService
import de.frederikkohler.bauchglueck.services.RecipeDatabaseService
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
        single { RecipeCategoryDatabaseService() }
        single { RecipeDatabaseService() }
    }

    install(Koin){
        modules(appModule)
    }
}