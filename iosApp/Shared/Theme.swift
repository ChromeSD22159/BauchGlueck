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
    private let lightTimerTextRegular = Color(uiColor: UIColor(rgb: 0xFFFFF))

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
    private let darkTimerTextRegular = Color(uiColor: UIColor(rgb: 0xFFFFFF))

    let cornerRadius: CGFloat = 16
    let paddingHorizontal: CGFloat = 16
    let paddingVertical: CGFloat = 8
    
    enum ColorType {
        case background, backgroundVariant, primary, primaryVariant, secondary, secondaryVariant, textBackground, textComplimentary, textRegular, timer
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
            case (.dark, .timer): return darkTimerTextRegular

            case (.light, .background): return lightBackground
            case (.light, .backgroundVariant): return lightBackgroundVariant
            case (.light, .primary): return lightPrimary
            case (.light, .primaryVariant): return lightPrimaryVariant
            case (.light, .secondary): return lightSecondary
            case (.light, .secondaryVariant): return lightSecondaryVariant
            case (.light, .textBackground): return lightTextBackground
            case (.light, .textComplimentary): return lightTextComplimentary
            case (.light, .textRegular): return lightTextRegular
            case (.light, .timer): return lightTimerTextRegular
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
    
    enum GradientType {
        case primary
    }
    
    func gradient(_ type: GradientType) -> LinearGradient { 
        switch ( type) {
            case (.primary): return gradient(array: [color(.primary), color(.primaryVariant)])
        }
    }
    
    func changeTheme(_ colorScheme: ColorScheme) {
        scheme = colorScheme
    }
    
    @ViewBuilder func backgroundImageWithOutImage(
        background: ColorType = .background,
        backgroundOpacity: Double
    ) -> some View {
        VStack(alignment: .trailing) {
            HStack(alignment: .top) {
                Spacer()
                ZStack(alignment: .topTrailing) {
                    
                    Image(.waveBehinde)
                        .opacity(0.3)
                        .frame(width: 266.15442, height: 283.81583, alignment: .topTrailing)
                    
                    Image(.waveAbove)
                        .opacity(0.3)
                        .frame(width: 266.15442, height: 283.81583, alignment: .topTrailing)
               }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(self.color(background).opacity(backgroundOpacity))
        .edgesIgnoringSafeArea(.all)
    }
}

extension UIColor {
   convenience init(red: Int, green: Int, blue: Int) {
       assert(red >= 0 && red <= 255, "Invalid red component")
       assert(green >= 0 && green <= 255, "Invalid green component")
       assert(blue >= 0 && blue <= 255, "Invalid blue component")

       self.init(red: CGFloat(red) / 255.0, green: CGFloat(green) / 255.0, blue: CGFloat(blue) / 255.0, alpha: 1.0)
   }

   convenience init(rgb: Int) {
       self.init(
           red: (rgb >> 16) & 0xFF,
           green: (rgb >> 8) & 0xFF,
           blue: rgb & 0xFF
       )
   }
}
