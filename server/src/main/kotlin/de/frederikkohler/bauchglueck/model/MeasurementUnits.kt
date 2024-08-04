package de.frederikkohler.bauchglueck.model

import org.jetbrains.exposed.sql.Table

object MeasurementUnits : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val displayName = varchar("displayName", 255)
    override val primaryKey = PrimaryKey(id)
}