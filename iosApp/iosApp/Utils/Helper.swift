//
//  Helper.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import UIKit

class Helper {
    static var shared = Helper()
    func printFonts(_ bool: Bool) {
        if bool {
            for familyName in UIFont.familyNames {
                print(familyName)
                
                for fontName in UIFont.fontNames(forFamilyName: familyName) {
                    print("-- \(fontName)")
                }
            }
        }
    }
}
