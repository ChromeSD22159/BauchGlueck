//
//  Recipe.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct Recipe: Identifiable, Codable {
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
