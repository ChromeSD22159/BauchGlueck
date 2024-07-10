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


class Theme: ObservableObject {
    var scheme: ColorScheme = .light
    
    // Light Mode Farben
    private let lightBackground = Color("Background")
    private let lightBackgroundVariant = Color("Background Variante")
    private let lightPrimary = Color("Primary")
    private let lightPrimaryVariant = Color("Primary Variante")
    private let lightSecondary = Color("Secondary")
    private let lightSecondaryVariant = Color("Secondary Variante")
    private let lightTextBackground = Color("Text Background")
    private let lightTextComplimentary = Color("Text Complimentary")
    private let lightTextRegular = Color("Text Regular")

    // Dark Mode Farben
    private let darkBackground = Color("Background Variante")
    private let darkBackgroundVariant = Color("Background")
    private let darkPrimary = Color("Primary Variante")
    private let darkPrimaryVariant = Color("Primary")
    private let darkSecondary = Color("Secondary Variante")
    private let darkSecondaryVariant = Color("Secondary")
    private let darkTextBackground = Color("Text Background")
    private let darkTextComplimentary = Color("Text Complimentary")
    private let darkTextRegular = Color("Text Regular")

    let cornerRadius: CGFloat = 16
    let paddingHorizontal: CGFloat = 16
    let paddingVertical: CGFloat = 8
    
    enum ColorType {
        case background, backgroundVariant, primary, primaryVariant, secondary, secondaryVariant, textBackground, textComplimentary, textRegular
    }

    // Funktion zur Farbauswahl je nach Farbschema
    func color(_ type: ColorType) -> Color {
        switch (scheme, type) {
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
    
    func changeTheme(_ colorScheme: ColorScheme) {
        scheme = colorScheme
    }
}
