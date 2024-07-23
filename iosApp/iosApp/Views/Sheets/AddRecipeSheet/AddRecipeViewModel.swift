//
//  AddRecipeViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

class AddRecipeViewModel: ObservableObject {
    @Published var isAddRecipeSheet: Bool = false
    @Published var showImageSheet: Bool = false
    
    @Published var segmentedOtions = AddRecipeSegmentedOtions.allCases

    @Published var selectedSegmentedOption: AddRecipeSegmentedOtions = .infomation
    
    @Published var recipeImage = UIImage()
    
    @Published var recipe: Recipe
    
    private let nilRecipe = Recipe(
        id: "",
        user_id: "",
        title: "",
        recipeCategory: .none,
        portion_size: "",
        preparation_time: "",
        cooking_time: "",
        ingredients: [Ingredients(val: "", name: "", form: nil, unit: .gram)], 
        preparation: "",
        rating: 0,
        notes: "",
        image: "",
        is_private: false,
        last_updated: Date()
    )
    
    func reset() {
        recipe = nilRecipe
        recipeImage = UIImage()
    }
    
    init() {
        recipe = nilRecipe
    }
    
    var bindingRecipeCategory: Binding<RecipeCategories> {
        Binding(
            get: { self.recipe.recipeCategory },
            set: { newValue in
                self.recipe.recipeCategory = newValue
            }
        )
    }
}

enum AddRecipeSegmentedOtions: String, CaseIterable {
    case infomation = "Infomation"
    case image = "Image"
    case ingredients = "Ingredients"
    case preparation = "Preparation"
    case notes = "Notes"
}

enum RecipeCategories: String, CaseIterable {
    case none = "Not categorized"
    case highProtein = "High Protein"
    case lightMeals = "Light Meals"
    case pureed = "Pureed"
    case soupsAndStews = "Soups & Stews"
    case smoothiesAndShakes = "Smoothies & Shakes"
    case vegetarian = "Vegetarian"
    case snacks = "Snacks"
    case desserts = "Desserts"
}

struct Recipe {
    var id: String
    var user_id: String
    var title: String
    var recipeCategory: RecipeCategories
    var portion_size: String
    var preparation_time: String
    var cooking_time: String
    var ingredients: [Ingredients]
    var preparation: String
    var rating: Int
    var notes: String
    var image: String
    var is_private: Bool
    var last_updated: Date
}

struct Ingredients {
    var val: String // Menge (z.B. "2")
    var name: String // Name der Zutat (z.B. "Zwiebel")
    var form: IngredientForm? = nil // Optionale Form (z.B. .gehackt)
    var unit: MeasurementUnit // Maßeinheit (z.B. .stück)
}

enum IngredientForm: String, CaseIterable {
    case chopped = "Chopped"
    case diced = "Diced"
    case grated = "Grated"
    case sliced = "Sliced"
    case pieces = "Pieces"
    case pureed = "Pureed"
    case whole = "Whole"
    case ground = "Ground"
    case cut = "Cut"
    case minced = "Minced"
    case cubed = "Cubed"
}

enum MeasurementUnit: String, CaseIterable {
    case gram = "g"
    case kilogram = "kg"
    case milliliter = "ml"
    case liter = "l"
    case teaspoon = "tsp"
    case tablespoon = "tbsp"
    case cup = "cup"
    case piece = "piece"
    case pinch = "pinch"
}
