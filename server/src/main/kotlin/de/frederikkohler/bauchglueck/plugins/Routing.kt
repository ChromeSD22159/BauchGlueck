package de.frederikkohler.bauchglueck.plugins

import de.frederikkohler.bauchglueck.services.IngredientFormsDatabaseService
import de.frederikkohler.bauchglueck.services.MeasurementUnitsDatabaseService
import io.ktor.server.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import de.frederikkohler.bauchglueck.model.recipe.IngredientForm
import de.frederikkohler.bauchglueck.model.recipe.MeasurementUnit
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    routing {
        route("/ingredient_forms") {
            val service: IngredientFormsDatabaseService by inject()

            post {
                val ingredientForm = call.receive<IngredientForm>()
                val addedIngredientForm = service.addIngredientForm(ingredientForm)
                if (addedIngredientForm != null) {
                    call.respond(HttpStatusCode.Created, addedIngredientForm)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to add ingredient form")
                }
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@put
                }
                val ingredientForm = call.receive<IngredientForm>().copy(id = id)
                val updatedIngredientForm = service.editIngredientForm(ingredientForm)
                if (updatedIngredientForm != null) {
                    call.respond(HttpStatusCode.OK, updatedIngredientForm)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update ingredient form")
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@delete
                }
                val deleted = service.deleteIngredientForm(id)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete ingredient form")
                }
            }

            get("/") {
                val ingredientForms = CoroutineScope(Dispatchers.IO).async {
                    service.listAllIngredientForms()
                }
                call.respond(HttpStatusCode.OK, ingredientForms)
            }
        }

        route("/measurementUnits") {
            val service: MeasurementUnitsDatabaseService by inject() // Assuming you have this service

            post {
                val formParameters = call.receiveParameters()
                val displayName = formParameters["displayName"]
                val symbol = formParameters["symbol"]

                if (displayName == null || symbol == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing displayName or symbol parameter")
                    return@post
                }

                try {
                    val measurementUnit = MeasurementUnit(null, displayName, symbol) // Create MeasurementUnit without ID
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

            get("/") {
                val measurementUnits = CoroutineScope(Dispatchers.IO).async {
                    service.listAllMeasurementUnits()
                }.await()
                call.respond(HttpStatusCode.OK, measurementUnits)
            }
        }
    }
}