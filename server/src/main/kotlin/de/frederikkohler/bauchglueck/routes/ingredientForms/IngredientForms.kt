package de.frederikkohler.bauchglueck.routes.ingredientForms

import de.frederikkohler.bauchglueck.services.IngredientFormsDatabaseService
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
import model.recipe.IngredientForm
import org.koin.ktor.ext.inject

fun Application.ingredientForms() {
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
    }
}