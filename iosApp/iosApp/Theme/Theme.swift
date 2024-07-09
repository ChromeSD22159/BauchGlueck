//
//  Theme.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 09.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import UIKit


class Theme {
    static let shared = Theme()

    @Environment(\.colorScheme) var colorScheme
    
    private init() {}

    // Light Mode Farben
    let lightBackground = Color("Background")
    let lightBackgroundVariant = Color("Background Variante")
    let lightPrimary = Color("Primary")
    let lightPrimaryVariant = Color("Primary Variante")
    let lightSecondary = Color("Secondary")
    let lightSecondaryVariant = Color("Secondary Variante")
    let lightTextBackground = Color("Text Background")
    let lightTextComplimentary = Color("Text Complimentary")
    let lightTextRegular = Color("Text Regular")

    // Dark Mode Farben
    let darkBackground = Color("Background Variante")
    let darkBackgroundVariant = Color("Background")
    let darkPrimary = Color("Primary Variante")
    let darkPrimaryVariant = Color("Primary")
    let darkSecondary = Color("Secondary Variante")
    let darkSecondaryVariant = Color("Secondary")
    let darkTextBackground = Color("Text Background")
    let darkTextComplimentary = Color("Text Complimentary")
    let darkTextRegular = Color("Text Regular")

    let cornerRadius: CGFloat = 16
    let paddingHorizontal: CGFloat = 16
    let paddingVertical: CGFloat = 8
    
    enum ColorType {
        case background, backgroundVariant, primary, primaryVariant, secondary, secondaryVariant, textBackground, textComplimentary, textRegular
    }

    // Funktion zur Farbauswahl je nach Farbschema
    func color(_ type: ColorType) -> Color {
        switch (colorScheme, type) {
            case (.dark, .background): return darkBackground
            case (.dark, .backgroundVariant): return darkBackgroundVariant
            case (.dark, .primary): return darkPrimary
            case (.dark, .primaryVariant): return darkPrimaryVariant
            case (.dark, .secondary): return darkSecondary
            case (.dark, .secondaryVariant): return darkSecondaryVariant
            case (.dark, .textBackground): return darkTextBackground
            case (.dark, .textComplimentary): return darkTextComplimentary
            case (.dark, .textRegular): return darkTextRegular

            case (.light, .background): return lightBackground
            case (.light, .backgroundVariant): return lightBackgroundVariant
            case (.light, .primary): return lightPrimary
            case (.light, .primaryVariant): return lightPrimaryVariant
            case (.light, .secondary): return lightSecondary
            case (.light, .secondaryVariant): return lightSecondaryVariant
            case (.light, .textBackground): return lightTextBackground
            case (.light, .textComplimentary): return lightTextComplimentary
            case (.light, .textRegular): return lightTextRegular
            case (_, _): return .clear
        }
    }
    
    func gradient(array: [Color]) -> LinearGradient {
        return LinearGradient(
            colors: array,
            startPoint: .top,
            endPoint: .bottom
        )
    }
}
