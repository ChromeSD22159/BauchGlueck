package de.frederikkohler.bauchglueck.tables

import org.jetbrains.exposed.sql.Table

object MeasurementUnits : Table() {
    val id = integer("id").autoIncrement().uniqueIndex()
    val displayName = varchar("displayName", 255)
    var symbol = varchar("symbol", 255)
    override val primaryKey = PrimaryKey(id)
}