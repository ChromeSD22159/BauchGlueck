package de.frederikkohler.bauchglueck.plugins

import de.frederikkohler.bauchglueck.services.IngredientFormsDatabaseService
import de.frederikkohler.bauchglueck.services.MeasurementUnitsDatabaseService
import de.frederikkohler.bauchglueck.services.RecipeCategoryDatabaseService
import de.frederikkohler.bauchglueck.services.RecipeDatabaseService
import io.ktor.server.application.*
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import model.recipe.IngredientForm
import model.recipe.MeasurementUnit
import model.recipe.Recipe
import model.recipe.RecipeCategory
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    measurementUnitsRoute()

    recipeCategoryRoute()

    ingredientForms()

    recipe()

}

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
             * GET /recipes/
             * Retrieves a list of all recipes or searches for recipes based on query parameters.
             * Query Parameters:
             * - q: Search query (optional)
             * - categoryId: Filter by recipe category ID (optional)
             * Returns:
             * - 200 OK: A list of recipes matching the search criteria or all recipes if no criteria are provided
             */
            get("s/") {
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

fun Application.measurementUnitsRoute() {
    routing {
        route("/measurementUnits") {
            val service: MeasurementUnitsDatabaseService by inject()

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
                try {
                    val measurementUnit = MeasurementUnit(null, formParameters.displayName, formParameters.symbol)
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
             * GET /measurementUnits/
             * Retrieves a list of all measurement units.
             * Returns:
             * - 200 OK: A list of all measurement units.
             *
             * Example using curl:
             * ```bash
             * curl http://localhost:8080/measurementUnits/
             * ```
             */
            get("/") {
                val measurementUnits = CoroutineScope(Dispatchers.IO).async {
                    service.listAllMeasurementUnits()
                }.await()
                println("measurementUnits fetched: ${measurementUnits.size}")
                call.respond(HttpStatusCode.OK, measurementUnits)
            }
        }
    }
}

fun Application.recipeCategoryRoute() {
    routing {
        val service: RecipeCategoryDatabaseService by inject()

        /**
         * POST /recipeCategory
         * Creates a new measurement unit.
         * Expects a JSON body containing the 'displayName' and 'symbol' of the measurement unit.
         * Returns:
         * - 201 Created: The newly created measurement unit.
         * - 400 Bad Request: If the request data is invalid.
         * - 500 Internal Server Error: If there's an error adding the measurement unit to the database.
         * curl -X POST http://localhost:8080/measurementUnits -H "Content-Type: application/json" -d '{ "id": 1, "displayName": "Kilogram", "symbol": "kg"}'
         */
        post("/recipeCategory") {
            val formParameters = call.receive<RecipeCategory>()
            try {
                val recipeCategory = RecipeCategory(0, formParameters.displayName)
                val addedRecipeCategory = service.addCategory(recipeCategory)

                if (addedRecipeCategory != null) {
                    call.respond(HttpStatusCode.Created, addedRecipeCategory)
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Failed to add measurement unit"
                    )
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request data")
            }
        }

        /**
         * GET /recipeCategories
         * Retrieves a list of all measurement units.
         * Returns:
         * - 200 OK: A list of all measurement units.
         *
         * Example using curl:
         * ```bash
         * curl http://localhost:8080/recipeCategories
         * ```
         */
        get("/recipeCategories/") {
            val measurementUnits = CoroutineScope(Dispatchers.IO).async {
                service.listAllCategories()
            }.await()
            call.respond(HttpStatusCode.OK, measurementUnits)
        }

        /**
         * PUT /recipeCategory/{id}
         * Updates an existing recipeCategory unit.
         * Expects a JSON body containing the updated 'displayName' and the 'id' in the path parameter.
         * Returns:
         * - 200 OK: The updated recipeCategory unit.
         * - 400 Bad Request: If the ID is invalid or missing.
         * - 500 Internal Server Error: If there's an error updating the measurement unit in the database.
         *
         * Example using curl:
         * ```bash
         * curl -X PUT http://localhost:8080/recipeCategory/2 -H "Content-Type: application/json" -d '{ "displayName": "test" }
         * ```
         */
        put("/recipeCategory/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@put
            }

            val recipeCategory = call.receive<RecipeCategory>().copy(id = id)

            val updatedRecipeCategory = service.editCategory(recipeCategory)

            if (updatedRecipeCategory != null) {
                call.respond(HttpStatusCode.OK, updatedRecipeCategory)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to update measurement unit")
            }
        }

        /**
         * DELETE /recipeCategory/{id}
         * Deletes a recipeCategory unit by its ID.
         * Expects the 'id' in the path parameter.
         * Returns:
         * - 204 No Content: If the deletion was successful.
         * - 400 Bad Request: If the ID is invalid or missing.
         * - 500 Internal Server Error: If there's an error deleting the measurement unit from the database.
         *
         * Example using curl:
         * ```bash
         * curl -X DELETE http://localhost:8080/recipeCategory/123 # Replace 123 with the actual ID
         * ```
         */
        delete("/recipeCategory/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID")
                return@delete
            }

            val deleted = service.deleteCategory(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete measurement unit")
            }
        }
    }
}
