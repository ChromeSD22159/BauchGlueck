package de.frederikkohler.bauchglueck.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Recipes : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val userID = varchar("userId", 255)
    val title = varchar("title", 255)
    val recipeCategory = reference("recipeCategoryId", RecipeCategories.id)
    val portionSize = varchar("portionSize", 255)
    val preparationTime = varchar("preparationTime", 255)
    val cookingTime = varchar("cookingTime", 255)
    val ingredient = reference("ingredientId", Ingredients.id)
    val preparation = text("preparation")
    val rating = integer("rating")
    val notes = text("notes")
    val titleImage = varchar("titleImage", 255)
    val isPrivate = bool("isPrivate")
    val created = datetime("created").defaultExpression(CurrentDateTime)
    val lastUpdated = datetime("lastUpdated").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}