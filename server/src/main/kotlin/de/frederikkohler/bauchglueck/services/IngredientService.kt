package de.frederikkohler.bauchglueck.services

import de.frederikkohler.bauchglueck.model.IngredientForms
import de.frederikkohler.bauchglueck.model.Ingredients
import de.frederikkohler.bauchglueck.model.MeasurementUnits
import model.recipe.Ingredient
import model.recipe.IngredientForm
import model.recipe.MeasurementUnit
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface IngredientService {
    suspend fun addIngredient(ingredient: Ingredient): Ingredient?
    suspend fun editIngredient(ingredient: Ingredient): Ingredient?
    suspend fun deleteIngredient(ingredientID: Int): Boolean
    suspend fun listAllIngredients(): List<Ingredient>
}

class IngredientsDatabaseService : IngredientService {
    // Function to map a ResultRow to an Ingredient object
    private fun resultRowToIngredient(row: ResultRow): Ingredient {
        return Ingredient(
            id = row[Ingredients.id],
            value = row[Ingredients.value],
            name = row[Ingredients.name],
            form = row[Ingredients.form]?.let { formId ->
                IngredientForms.select { IngredientForms.id eq formId }
                    .map { formRow -> IngredientForm(id = formRow[IngredientForms.id], displayName = formRow[IngredientForms.displayName]) }
                    .singleOrNull()
            },
            unit = MeasurementUnits.select { MeasurementUnits.id eq row[Ingredients.unit] }
                .map { unitRow -> MeasurementUnit(id = unitRow[MeasurementUnits.id], displayName = unitRow[MeasurementUnits.displayName]) }
                .single()
        )
    }

    // Function to perform database queries in a coroutine-friendly way
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }

    // Function to add a new ingredient
    override suspend fun addIngredient(ingredient: Ingredient): Ingredient? = dbQuery {
        val insertStatement = Ingredients.insert {
            it[value] = ingredient.value
            it[name] = ingredient.name
            it[form] = ingredient.form?.id
            it[unit] = ingredient.unit.id
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToIngredient(it) }
    }

    // Function to edit an existing ingredient
    override suspend fun editIngredient(ingredient: Ingredient): Ingredient? = dbQuery {
        val updateStatement = Ingredients.update({ Ingredients.id eq ingredient.id }) {
            it[value] = ingredient.value
            it[name] = ingredient.name
            it[form] = ingredient.form?.id
            it[unit] = ingredient.unit.id
        }
        if (updateStatement > 0) findIngredientById(ingredient.id) else null
    }

    // Function to delete an ingredient by its ID
    override suspend fun deleteIngredient(ingredientID: Int): Boolean = dbQuery {
        Ingredients
            .deleteWhere { id eq ingredientID } > 0
    }

    // Function to list all ingredients
    override suspend fun listAllIngredients(): List<Ingredient> = dbQuery {
        Ingredients
            .selectAll()
            .map { resultRowToIngredient(it) }
    }

    // Function to find an ingredient by its ID
    private suspend fun findIngredientById(ingredientID: Int): Ingredient? = dbQuery {
        Ingredients
            .selectAll()
            .where { Ingredients.id eq ingredientID }
            .map { resultRowToIngredient(it) }
            .singleOrNull()
    }
}