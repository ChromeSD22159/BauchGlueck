package de.frederikkohler.bauchglueck.services

import de.frederikkohler.bauchglueck.model.IngredientForms
import model.recipe.IngredientForm
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

interface IngredientFormsService {
    suspend fun addIngredientForm(ingredientForm: IngredientForm): IngredientForm?
    suspend fun editIngredientForm(ingredientForm: IngredientForm): IngredientForm?
    suspend fun deleteIngredientForm(ingredientFormID: Int): Boolean
    suspend fun listAllIngredientForms(): List<IngredientForm>
}

class IngredientFormsDatabaseService : IngredientFormsService {
    // Function to map a ResultRow to an IngredientForm object
    private fun resultRowToIngredientForm(row: ResultRow): IngredientForm {
        return IngredientForm(
            id = row[IngredientForms.id],
            displayName = row[IngredientForms.displayName]
        )
    }

    // Function to perform database queries in a coroutine-friendly way
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }

    // Function to add a new ingredient form
    override suspend fun addIngredientForm(ingredientForm: IngredientForm): IngredientForm? = dbQuery {
        val insertStatement = IngredientForms.insert {
            it[displayName] = ingredientForm.displayName
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowToIngredientForm(it) }
    }

    // Function to edit an existing ingredient form
    override suspend fun editIngredientForm(ingredientForm: IngredientForm): IngredientForm? = dbQuery {
        ingredientForm.id.let { it ->
            if (it == null) return@let null

            val updateStatement = IngredientForms.update({ IngredientForms.id eq it }) {
                it[displayName] = ingredientForm.displayName
            }
            if (updateStatement > 0) ingredientForm.id?.let { findIngredientFormById(it) } else null
        }
    }

    // Function to delete an ingredient form by its ID
    override suspend fun deleteIngredientForm(ingredientFormID: Int): Boolean = dbQuery {
         IngredientForms.deleteWhere { id eq ingredientFormID } > 0
    }

    // Function to list all ingredient forms
    override suspend fun listAllIngredientForms(): List<IngredientForm> = dbQuery {
        IngredientForms.selectAll().map { resultRowToIngredientForm(it) }
    }

    // Function to find an ingredient form by its ID
    private suspend fun findIngredientFormById(ingredientFormID: Int): IngredientForm? = dbQuery {
        IngredientForms.selectAll().where { IngredientForms.id eq ingredientFormID }
            .map { resultRowToIngredientForm(it) }
            .singleOrNull()
    }
}