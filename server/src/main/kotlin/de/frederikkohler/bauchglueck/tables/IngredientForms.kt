package de.frederikkohler.bauchglueck.tables

import org.jetbrains.exposed.sql.Table

object IngredientForms : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val displayName = varchar("displayName", 255)
    override val primaryKey = PrimaryKey(id)
}