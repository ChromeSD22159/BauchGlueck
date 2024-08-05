package de.frederikkohler.bauchglueck.routes

import de.frederikkohler.bauchglueck.routes.ingredientForms.ingredientForms
import de.frederikkohler.bauchglueck.routes.measurementUnits.measurementUnitsRoute
import de.frederikkohler.bauchglueck.routes.recipe.recipe
import io.ktor.server.application.*

fun Application.configureRouting() {

    measurementUnitsRoute()

    ingredientForms()

    recipe()

}
