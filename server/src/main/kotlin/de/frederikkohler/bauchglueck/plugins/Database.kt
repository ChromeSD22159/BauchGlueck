package de.frederikkohler.bauchglueck.plugins

import de.frederikkohler.bauchglueck.model.IngredientForms
import de.frederikkohler.bauchglueck.model.Ingredients
import de.frederikkohler.bauchglueck.model.MeasurementUnits
import de.frederikkohler.bauchglueck.model.RecipeCategories
import de.frederikkohler.bauchglueck.model.RecipeIngredients
import de.frederikkohler.bauchglueck.model.Recipes
import de.frederikkohler.bauchglueck.utils.DatabaseService
import de.frederikkohler.bauchglueck.utils.EnvService
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import org.koin.ktor.ext.get

fun Application.configureDatabases(
    envService: EnvService =get()
): Database {
    val db: Database = try {
        DatabaseService(
            dotenv = envService.getEnv()
        ).connection ?: throw SQLException("Datenbankverbindung ist null.")
    } catch (e: SQLException) {
        println("Connection failed: " + e.message)
        throw e
    }

    var tables = arrayOf(
        RecipeIngredients,
        Recipes,
        Ingredients,
        IngredientForms,
        MeasurementUnits,
        RecipeCategories)



    transaction(db){

        SchemaUtils.createMissingTablesAndColumns(
            Recipes,
            RecipeIngredients,
            RecipeCategories,
            MeasurementUnits,
            IngredientForms,
            Ingredients
        )

        launch(Dispatchers.IO) {
            DatabaseService(
                dotenv = envService.getEnv()
            )
        }
    }

    return db
}

suspend fun <T> dbQuery(block:suspend ()->T):T{
    return newSuspendedTransaction(Dispatchers.IO) { block() }
}