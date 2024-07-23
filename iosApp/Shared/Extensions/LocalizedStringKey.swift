//
//  LocalizedStringKey.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

extension LocalizedStringKey {
    var stringKey: String {
        let mirror = Mirror(reflecting: self)
        let key = mirror.children.first(where: { $0.label == "key" })?.value as? String
        return key ?? ""
    }
}
