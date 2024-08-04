package de.frederikkohler.bauchglueck.services

import de.frederikkohler.bauchglueck.model.RecipeCategories
import de.frederikkohler.bauchglueck.model.recipe.RecipeCategory
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

interface RecipeCategoryService {
    suspend fun addCategory(category: RecipeCategory): RecipeCategory?
    suspend fun editCategory(category: RecipeCategory): RecipeCategory?
    suspend fun deleteCategory(categoryID: Int): Boolean
    suspend fun listAllCategories(): List<RecipeCategory>
    suspend fun findCategoryById(categoryID: Int): RecipeCategory?
}

class RecipeCategoryDatabaseService: RecipeCategoryService {
    private fun resultRowCategory(row: ResultRow): RecipeCategory {
        return RecipeCategory(
            id = row[RecipeCategories.id],
            displayName = row[RecipeCategories.displayName]
        )
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }

    // Function to add a new category
    override suspend fun addCategory(category: RecipeCategory): RecipeCategory? = dbQuery {
        val insertStatement = RecipeCategories.insert {
            it[displayName] = category.displayName
        }
        insertStatement.resultedValues?.singleOrNull()?.let { resultRowCategory(it) }
    }

    // Function to edit an existing category
    override suspend fun editCategory(category: RecipeCategory): RecipeCategory? = dbQuery {
        val updateStatement = RecipeCategories.update({ RecipeCategories.id eq category.id }) {
            it[displayName] = category.displayName
        }
        if (updateStatement > 0) findCategoryById(category.id) else null
    }

    // Function to delete a category by its ID
    override suspend fun deleteCategory(categoryID: Int) : Boolean = dbQuery {
        RecipeCategories
            .deleteWhere { id eq categoryID } > 0
    }

    // Function to list all categories
    override suspend fun listAllCategories(): List<RecipeCategory> = dbQuery {
        RecipeCategories
            .selectAll()
            .map { resultRowCategory(it) }
    }

    // Function to find a category by its ID
    override suspend fun findCategoryById(categoryID: Int): RecipeCategory? = dbQuery {
        RecipeCategories
            .selectAll()
            .where { RecipeCategories.id eq categoryID }
            .map { resultRowCategory(it) }
            .singleOrNull()
    }
}