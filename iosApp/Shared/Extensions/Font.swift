//
//  Font.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 18.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

extension Font {
    static func kodchasanBold(size: Font.Size) -> Font {
        return .custom("Kodchasan-Bold", size: size.rawValue)
    }
    
    static func kodchasanBold(size: CGFloat) -> Font {
        return .custom("Kodchasan-Bold", size: size)
    }

    static func kodchasanRegular(size: Font.Size) -> Font {
        return .custom("Kodchasan-Regular", size: size.rawValue)
    }
    
    static func kodchasanRegular(size: CGFloat) -> Font {
        return .custom("Kodchasan-Regular", size: size)
    }
    
    static func seat(size: CGFloat) -> Font {
        return .custom("Audiowide", size: size)
    }
    
    static func seat(size: Font.Size) -> Font {
        return .custom("Audiowide", size: size.rawValue)
    }

    enum Size: CGFloat {
        case largeTitle = 34
        case title = 28
        case title2 = 22
        case title3 = 20
        case headline, body = 17
        case callout = 16
        case subheadline = 15
        case footnote = 13
        case caption = 12
        case caption2 = 11
    }
}
