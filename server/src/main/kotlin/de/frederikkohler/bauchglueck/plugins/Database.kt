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
import model.recipe.RecipeCategory
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

    val tables = arrayOf(
                    RecipeIngredients,
                    Recipes,
                    Ingredients,
                    IngredientForms,
                    MeasurementUnits,
                    RecipeCategories
                )

    transaction(db){

        initializeTables(tables)
        initializeMeasurementUnits()
        initializeRecipeCategories()

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

fun initializeTables(tables: Array<Table>) {
    SchemaUtils.createMissingTablesAndColumns(
        *tables
    )
}

fun initializeMeasurementUnits() {
    if (MeasurementUnits.selectAll().empty()) {
            MeasurementUnits.batchInsert(listOf(
                "Gramm" to "g",
                "Kilogramm" to "kg",
                "Milliliter" to "ml",
                "Liter" to "l",
                "Teelöffel" to "TL",
                "Esslöffel" to "EL",
                "Tasse" to "Tasse",
                "Prise" to "Prise",
                "Stück" to "Stück"
            )) { (displayName, symbol) ->
                this[MeasurementUnits.displayName] = displayName
                this[MeasurementUnits.symbol] = symbol
            }
    }
}

fun initializeRecipeCategories() {
    val recipeCategories = listOf(
        RecipeCategory(1, "Flüssigphase"),
        RecipeCategory(2, "Pürierte Kost"),
        RecipeCategory(3, "Weiche Kost"),
        RecipeCategory(4, "Normalkost"),
        RecipeCategory(5, "Proteinreich"),
        RecipeCategory(6, "Zuckerarm"),
        RecipeCategory(7, "Laktosefrei"),
        RecipeCategory(8, "Glutenfrei"),
        RecipeCategory(9, "Vegetarisch"),
        RecipeCategory(10, "Vegan")
    )

    if (RecipeCategories.selectAll().empty()) {
        RecipeCategories.batchInsert(recipeCategories) { recipeCategory ->
            this[RecipeCategories.displayName] = recipeCategory.displayName
        }
    }
}