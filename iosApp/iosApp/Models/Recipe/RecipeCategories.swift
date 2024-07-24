//
//  RecipeCategories.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

enum RecipeCategories: String, CaseIterable, Codable { 
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
