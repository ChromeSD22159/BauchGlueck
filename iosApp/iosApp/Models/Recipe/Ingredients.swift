//
//  Ingredients.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

struct Ingredients: Codable { 
    var val: String // Menge (z.B. "2")
    var name: String // Name der Zutat (z.B. "Zwiebel")
    var form: IngredientForm? = nil // Optionale Form (z.B. .gehackt)
    var unit: MeasurementUnit // Maßeinheit (z.B. .stück)
}
