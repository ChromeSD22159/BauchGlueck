package de.frederikkohler.bauchglueck.services

import de.frederikkohler.bauchglueck.model.IngredientForms
import de.frederikkohler.bauchglueck.model.Ingredients
import de.frederikkohler.bauchglueck.model.MeasurementUnits
import de.frederikkohler.bauchglueck.model.RecipeCategories
import de.frederikkohler.bauchglueck.model.RecipeIngredients
import de.frederikkohler.bauchglueck.model.Recipes
import de.frederikkohler.bauchglueck.plugins.dbQuery
import de.frederikkohler.bauchglueck.model.recipe.Ingredient
import de.frederikkohler.bauchglueck.model.recipe.IngredientForm
import de.frederikkohler.bauchglueck.model.recipe.MeasurementUnit
import de.frederikkohler.bauchglueck.model.recipe.Recipe
import de.frederikkohler.bauchglueck.model.recipe.RecipeCategory
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface RecipeService {
    suspend fun addRecipe(recipe: Recipe): Recipe?
    suspend fun listAllRecipes(): List<Recipe>
}

class RecipeDatabaseService: RecipeService {
    private fun resultRowRecipe(row: ResultRow): Recipe {
        val recipeCategory = loadRecipeCategory(row[Recipes.recipeCategory])
        val ingredients = loadIngredients(row[Recipes.id])

        return Recipe(
            id = row[Recipes.id],
            userID = row[Recipes.userID].toString(),
            title = row[Recipes.title],
            recipeCategory = recipeCategory,
            portionSize = row[Recipes.portionSize],
            preparationTime = row[Recipes.preparationTime],
            cookingTime = row[Recipes.cookingTime],
            ingredients = ingredients,
            preparation = row[Recipes.preparation],
            rating = row[Recipes.rating],
            notes = row[Recipes.notes],
            image = row[Recipes.image],
            isPrivate = row[Recipes.isPrivate],
            created = row[Recipes.created].toString(),
            lastUpdated = row[Recipes.lastUpdated].toString()
        )
    }

    private fun loadRecipeCategory(recipeCategoryId: Int): RecipeCategory {
        return transaction {
            RecipeCategories.selectAll().where { RecipeCategories.id eq recipeCategoryId }
                .map { row ->
                    RecipeCategory(
                        id = row[RecipeCategories.id],
                        displayName = row[RecipeCategories.displayName]
                    )
                }
                .single()
        }
    }

    private fun loadIngredients(recipeId: Int): List<Ingredient> {
        return transaction {
            (RecipeIngredients innerJoin Ingredients)
                .selectAll().where { RecipeIngredients.recipe eq recipeId }
                .map { row ->
                    Ingredient(
                        id = row[Ingredients.id],
                        value = row[Ingredients.value],
                        name = row[Ingredients.name],
                        form = row[Ingredients.form]?.let { formId ->
                            IngredientForms.selectAll().where { IngredientForms.id eq formId }
                                .map { formRow ->
                                    IngredientForm(
                                        id = formRow[IngredientForms.id],
                                        displayName = formRow[IngredientForms.displayName]
                                    )
                                }
                                .singleOrNull()
                        },
                        unit = MeasurementUnits.selectAll()
                            .where { MeasurementUnits.id eq row[Ingredients.unit] }
                            .map { unitRow ->
                                MeasurementUnit(
                                    id = unitRow[MeasurementUnits.id],
                                    displayName = unitRow[MeasurementUnits.displayName],
                                    symbol = unitRow[MeasurementUnits.symbol]
                                )
                            }
                            .single()
                    )
                }
        }
    }

    override suspend fun addRecipe(recipe: Recipe): Recipe? = dbQuery {
        val insertStmt = Recipes.insert {
            it[userID] = recipe.userID
            it[title] = recipe.title
            it[recipeCategory] = recipe.recipeCategory.id
            it[portionSize] = recipe.portionSize
            it[preparationTime] = recipe.preparationTime
            it[cookingTime] = recipe.cookingTime
            it[preparation] = recipe.preparation
            it[rating] = recipe.rating
            it[notes] = recipe.notes
            it[image] = recipe.image
            it[isPrivate] = recipe.isPrivate
            it[created] = LocalDateTime.now()
            it[lastUpdated] = LocalDateTime.now()
        }
        insertStmt.resultedValues?.singleOrNull()?.let { resultRowRecipe(it) }
    }

    override suspend fun listAllRecipes(): List<Recipe> = dbQuery {
            Recipes
            .selectAll()
            .map { resultRowRecipe(it) }
    }
}