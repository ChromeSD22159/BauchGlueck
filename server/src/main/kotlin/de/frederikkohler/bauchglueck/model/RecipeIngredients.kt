package de.frederikkohler.bauchglueck.model

import org.jetbrains.exposed.sql.Table

object RecipeIngredients: Table() {
    val id = integer("id")
    val recipe = reference("recipeID", Recipes.id)
    val ingredient = reference("ingredientID", Ingredients.id)
    override val primaryKey = PrimaryKey(recipe)
}