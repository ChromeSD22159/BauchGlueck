package de.frederikkohler.bauchglueck.routes.measurementUnits

import de.frederikkohler.bauchglueck.services.MeasurementUnitsDatabaseService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import model.recipe.MeasurementUnit
import org.koin.ktor.ext.inject

fun Application.measurementUnitsRoute() {
    routing {
        route("/measurementUnits") {
            val service: MeasurementUnitsDatabaseService by inject() // Assuming you have this service

            /**
             * POST /measurementUnits
             * Creates a new measurement unit.
             * Expects a JSON body containing the 'displayName' and 'symbol' of the measurement unit.
             * Returns:
             * - 201 Created: The newly created measurement unit.
             * - 400 Bad Request: If the request data is invalid.
             * - 500 Internal Server Error: If there's an error adding the measurement unit to the database.
             * curl -X POST http://localhost:8080/measurementUnits -H "Content-Type: application/json" -d '{ "id": 1, "displayName": "Kilogram", "symbol": "kg"}'
             */
            post {
                val formParameters = call.receive<MeasurementUnit>()
                formParameters.symbol
                try {
                    val measurementUnit =
                        MeasurementUnit(null, formParameters.displayName, formParameters.symbol)
                    val addedMeasurementUnit = service.addMeasurementUnit(measurementUnit)

                    if (addedMeasurementUnit != null) {
                        call.respond(HttpStatusCode.Created, addedMeasurementUnit)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to add measurement unit")
                    }
                }  catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request data")
                }
            }


            /**
             * PUT /measurementUnits/{id}
             * Updates an existing measurement unit.
             * Expects a JSON body containing the updated 'displayName' and the 'id' in the path parameter.
             * Returns:
             * - 200 OK: The updated measurement unit.
             * - 400 Bad Request: If the ID is invalid or missing.
             * - 500 Internal Server Error: If there's an error updating the measurement unit in the database.
             *
             * Example using curl:
             * ```bash
             * curl -X PUT http://localhost:8080/measurementUnits/2 -H "Content-Type: application/json" -d '{ "displayName": "test", "symbol": "kg" }
             * ```
             */
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@put
                }

                val measurementUnit = call.receive<MeasurementUnit>().copy(id = id)
                val updatedMeasurementUnit = service.editMeasurementUnit(measurementUnit)

                if (updatedMeasurementUnit != null) {
                    call.respond(HttpStatusCode.OK, updatedMeasurementUnit)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update measurement unit")
                }
            }


            /**
             * DELETE /measurementUnits/{id}
             * Deletes a measurement unit by its ID.
             * Expects the 'id' in the path parameter.
             * Returns:
             * - 204 No Content: If the deletion was successful.
             * - 400 Bad Request: If the ID is invalid or missing.
             * - 500 Internal Server Error: If there's an error deleting the measurement unit from the database.
             *
             * Example using curl:
             * ```bash
             * curl -X DELETE http://localhost:8080/measurementUnits/123 # Replace 123 with the actual ID
             * ```
             */
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@delete
                }

                val deleted = service.deleteMeasurementUnit(id)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete measurement unit")
                }
            }


            /**
             * GET /measurementUnits
             * Retrieves a list of all measurement units.
             * Returns:
             * - 200 OK: A list of all measurement units.
             *
             * Example using curl:
             * ```bash
             * curl http://localhost:8080/measurementUnits
             * ```
             */
            get("/") {
                val measurementUnits = CoroutineScope(Dispatchers.IO).async {
                    service.listAllMeasurementUnits()
                }.await()
                call.respond(HttpStatusCode.OK, measurementUnits)
            }
        }
    }
}