package de.frederikkohler.bauchglueck.services

import de.frederikkohler.bauchglueck.model.MeasurementUnits
import de.frederikkohler.bauchglueck.plugins.dbQuery
import model.recipe.MeasurementUnit
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

interface MeasurementUnitService {
    suspend fun addMeasurementUnit(measurementUnit: MeasurementUnit): MeasurementUnit?
    suspend fun listAllMeasurementUnits(): List<MeasurementUnit>
    suspend fun editMeasurementUnit(measurementUnit: MeasurementUnit): MeasurementUnit?
    suspend fun deleteMeasurementUnit(id: Int): Boolean
}

class MeasurementUnitsDatabaseService : MeasurementUnitService {
    private fun resultRowMeasurementUnit(row: ResultRow): MeasurementUnit {
        return MeasurementUnit(
            id = row[MeasurementUnits.id],
            displayName = row[MeasurementUnits.displayName],
            symbol = row[MeasurementUnits.symbol]
        )
    }

    override suspend fun addMeasurementUnit(measurementUnit: MeasurementUnit): MeasurementUnit? = dbQuery {
        val insertResult = MeasurementUnits.insert {
            it[displayName] = measurementUnit.displayName
            it[symbol] = measurementUnit.symbol
        }

        insertResult.resultedValues?.singleOrNull()?.let { row ->
            MeasurementUnit(
                id = row[MeasurementUnits.id],
                displayName = row[MeasurementUnits.displayName],
                symbol = row[MeasurementUnits.symbol]
            )
        }
    }

    override suspend fun listAllMeasurementUnits(): List<MeasurementUnit> = dbQuery {
        MeasurementUnits
            .selectAll()
            .map { resultRowMeasurementUnit(it) }
    }

    override suspend fun editMeasurementUnit(measurementUnit: MeasurementUnit): MeasurementUnit? = dbQuery {

        if (measurementUnit.id == null) return@dbQuery null

        val updatedRows = MeasurementUnits.update({ MeasurementUnits.id eq measurementUnit.id!! }) {
            it[displayName] = measurementUnit.displayName
        }

        if (updatedRows > 0) measurementUnit else null
    }

    override suspend fun deleteMeasurementUnit(id: Int): Boolean = dbQuery {
        MeasurementUnits
            .deleteWhere { MeasurementUnits.id eq id } > 0
    }
}