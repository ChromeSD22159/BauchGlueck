package de.frederikkohler.bauchglueck.model

import org.jetbrains.exposed.sql.Table

object RecipeIngredients: Table() {
    val id = integer("id")
    val recipeID = reference("recipeID", Recipes.id)
    val ingredientID = reference("ingredientID", Ingredients.id)
    override val primaryKey = PrimaryKey(recipeID)
}