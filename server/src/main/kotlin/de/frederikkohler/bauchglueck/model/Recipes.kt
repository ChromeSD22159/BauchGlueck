package de.frederikkohler.bauchglueck.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Recipes : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val userID = varchar("userID", 255)
    val title = varchar("title", 255)
    val recipeCategory = reference("recipeCategoryID", RecipeCategories.id)
    val portionSize = varchar("portionSize", 255)
    val preparationTime = varchar("preparationTime", 255)
    val cookingTime = varchar("cookingTime", 255)
    val ingredients = reference("recipeIngredients", RecipeIngredients.id)
    val preparation = text("preparation")
    val rating = integer("rating")
    val notes = text("notes")
    val image = varchar("image", 255)
    val isPrivate = bool("isPrivate")
    val created = datetime("created").defaultExpression(CurrentDateTime)
    val lastUpdated = datetime("lastUpdated").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}