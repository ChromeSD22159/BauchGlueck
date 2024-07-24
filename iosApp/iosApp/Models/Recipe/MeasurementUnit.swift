//
//  MeasurementUnit.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

enum MeasurementUnit: String, CaseIterable, Codable {
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
