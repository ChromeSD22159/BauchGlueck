package de.frederikkohler.bauchglueck.routes.recipe

import de.frederikkohler.bauchglueck.services.RecipeDatabaseService
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
import model.recipe.Recipe
import org.koin.ktor.ext.inject

fun Application.recipe() {
    routing {
        route("/recipe") {
            val service: RecipeDatabaseService by inject()

            /**
             * POST /recipe
             * Creates a new recipe.
             * Expects a JSON body representing the recipe details.
             * Returns:
             * - 201 Created: The newly created recipe.
             * - 400 Bad Request: If the request data is invalid.
             * - 500 Internal Server Error: If there's an error adding the recipe to the database.
             */
            post {
                try {
                    val recipe = call.receive<Recipe>()
                    val addedRecipe = service.addRecipe(recipe)

                    if (addedRecipe != null) {
                        call.respond(HttpStatusCode.Created, addedRecipe)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to add recipe")
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request data")
                }
            }

            /**
             * PUT /recipe/{id}
             * Updates an existing recipe.
             * Expects a JSON body representing the updated recipe details and the 'id' in the path parameter.
             * Returns:
             * - 200 OK: The updated recipe.
             * - 400 Bad Request: If the ID is invalid or missing, or the request data is invalid.
             * - 404 Not Found: If the recipe with the given ID is not found.
             * - 500 Internal Server Error: If there's an error updating the recipe in the database.
             */
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@put
                }

                try {
                    val recipe = call.receive<Recipe>().copy(id = id)
                    val updatedRecipe = service.editRecipe(recipe)

                    call.respond(HttpStatusCode.OK, updatedRecipe)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request data")
                }
            }

            /**
             * DELETE /recipe/{id}
             * Deletes a recipe by its ID.
             * Expects the 'id' in the path parameter.
             * Returns:
             * - 204 No Content: If the deletion was successful.
             * - 400 Bad Request: If the ID is invalid or missing.
             * - 404 Not Found: If the recipe with the given ID is not found
             * - 500 Internal Server Error: If there's an error deleting the recipe from the database.
             */
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@delete
                }

                val deleted = service.deleteRecipe(id)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Recipe not found")
                }
            }

            /**
             * GET /recipe
             * Retrieves a list of all recipes or searches for recipes based on query parameters.
             * Query Parameters:
             * - q: Search query (optional)
             * - categoryId: Filter by recipe category ID (optional)
             * Returns:
             * - 200 OK: A list of recipes matching the search criteria or all recipes if no criteria are provided
             */
            get("/") {
                val query = call.request.queryParameters["q"]
                val categoryId = call.request.queryParameters["categoryId"]?.toIntOrNull()

                val recipes = if (query != null) {
                    service.searchRecipes(query)
                } else if (categoryId != null) {
                    service.getRecipesByCategory(categoryId)
                } else {
                    service.listAllRecipes()
                }

                call.respond(HttpStatusCode.OK, recipes)
            }

            /**
             * GET /recipe/{id}
             * Retrieves a specific recipe by its ID
             * Expects the 'id' in the path parameter.
             * Returns:
             * - 200 OK: The recipe with the given ID
             * - 400 Bad Request: If the ID is invalid or missing
             * - 404 Not Found: If the recipe with the given ID is not found
             */
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                    return@get
                }

                val recipe = service.getRecipeById(id)
                call.respond(HttpStatusCode.OK, recipe)
            }
        }
    }
}