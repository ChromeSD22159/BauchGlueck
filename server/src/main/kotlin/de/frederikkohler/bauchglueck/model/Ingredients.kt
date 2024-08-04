package de.frederikkohler.bauchglueck.model

import org.jetbrains.exposed.sql.Table

object Ingredients : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val value = varchar("value", 255)
    val name = varchar("name", 255)
    val form = reference("formID", IngredientForms.id).nullable()
    val unit = reference("unitID", MeasurementUnits.id)
    override val primaryKey = PrimaryKey(id)
}