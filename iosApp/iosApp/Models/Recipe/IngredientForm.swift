//
//  IngredientForm.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

enum IngredientForm: String, CaseIterable, Codable {
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
