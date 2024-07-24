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
        ingredients: [],
        preparation: "",
        rating: 0,
        notes: "",
        image: "",
        is_private: false,
        last_updated: Date()
    )
    
    func resetRecipeUIImage() {
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
    
    var isSendable: Bool {
        var missingFields: [String] = []

        if recipe.id == "" { missingFields.append("id") }
        if recipe.user_id == "" { missingFields.append("user_id") }
        if recipe.title == "" { missingFields.append("title") }
        if recipe.portion_size == "" { missingFields.append("portion_size") }
        if recipe.preparation_time == "" { missingFields.append("preparation_time") }
        if recipe.cooking_time == "" { missingFields.append("cooking_time") }
        if recipe.preparation == "" { missingFields.append("preparation") }

        if !missingFields.isEmpty {
            print("Folgende Pflichtfelder sind nicht ausgefüllt: \(missingFields.joined(separator: ", "))")
            return false
        }

        return true
    }
    
    func addRecipe() {
        if let userID = FirebaseAuthManager.shared.user?.uid {
            
            recipe.id = UUID().uuidString
            recipe.user_id = userID
            
            if isSendable {
                FirebaseRecipeManager.shared.addRecipe(recipe: recipe)
            }
        }
    }
}
