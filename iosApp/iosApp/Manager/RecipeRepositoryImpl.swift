import CoreData
import Shared
import Foundation

class RecipeRepositoryImpl: RecipeRepository {
    private let context: NSManagedObjectContext
    private let apiService: ApiService

    init(context: NSManagedObjectContext, apiService: ApiService) {
        self.context = context
        self.apiService = apiService
    }

    // Synchronize data from API to Core Data
    func syncData() async -> Bool {
        do {
            // Fetch categories
            let categories = try await apiService.fetchRecipeCategories()
            try saveCategories(categories: categories)

            // Fetch measurement units
            let units = try await apiService.fetchMeasurementUnits()
            try saveUnits(units: units)

            return true
        } catch {
            print("Failed to sync data: \(error)")
            return false
        }
    }

    private func saveCategories(categories: [RecipeCategory]) throws {
        let categoryEntities = categories.map { category -> RecipeCategoryEntity in
            let entity = RecipeCategoryEntity(context: self.context)
            entity.id = Int32(category.id)
            entity.displayName = category.displayName
            return entity
        }

        let existingCategories = try context.fetch(RecipeCategoryEntity.fetchRequest())
        existingCategories.forEach { context.delete($0) }

        categoryEntities.forEach { context.insert($0) }
        try context.save()
    }

    private func saveUnits(units: [MeasurementUnit]) throws {
        let unitEntities = units.map { unit -> MeasurementUnitEntity in
            let entity = MeasurementUnitEntity(context: self.context)
            entity.id = Int32(unit.id ?? 0)
            entity.displayName = unit.displayName
            entity.symbol = unit.symbol
            return entity
        }

        let existingUnits = try context.fetch(MeasurementUnitEntity.fetchRequest())
        existingUnits.forEach { context.delete($0) }

        unitEntities.forEach { context.insert($0) }
        try context.save()
    }

    func saveRecipe(recipe: Recipe) async -> Bool {
        guard recipe.isPrivate else {
            return false
        }

        let recipeEntity = RecipeEntity(context: self.context)
        recipeEntity.id = Int32(recipe.id)
        recipeEntity.userID = recipe.userID
        recipeEntity.title = recipe.title
        recipeEntity.recipeCategoryId = Int32(recipe.recipeCategory.id)
        recipeEntity.portionSize = recipe.portionSize
        recipeEntity.preparationTime = recipe.preparationTime
        recipeEntity.cookingTime = recipe.cookingTime
        recipeEntity.preparation = recipe.preparation
        recipeEntity.rating = Int32(recipe.rating)
        recipeEntity.notes = recipe.notes
        recipeEntity.titleImage = recipe.titleImage
        recipeEntity.isPrivate = recipe.isPrivate
        recipeEntity.created = recipe.created
        recipeEntity.lastUpdated = recipe.lastUpdated

        recipe.ingredients.forEach { ingredient in
            let ingredientEntity = IngredientEntity(context: self.context)
            ingredientEntity.id = Int32(ingredient.id)
            ingredientEntity.value = ingredient.value
            ingredientEntity.name = ingredient.name
            ingredientEntity.formId = ingredient.form?.id.flatMap { Int32($0) }
            ingredientEntity.unitId = Int32(ingredient.unit.id)
            context.insert(ingredientEntity)
        }

        do {
            try context.save()
            return true
        } catch {
            print("Failed to save recipe: \(error)")
            return false
        }
    }
}