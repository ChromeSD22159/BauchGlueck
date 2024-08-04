package de.frederikkohler.bauchglueck.model

import org.jetbrains.exposed.sql.Table

object RecipeImages : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val recipeID = reference("recipeID", Recipes.id)
    val imageUrl = varchar("imageUrl", 255)

    override val primaryKey = PrimaryKey(id)
}