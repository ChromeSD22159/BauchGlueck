//
//  String.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

extension String {
    var toLocalizedStringKey: LocalizedStringKey {
        return LocalizedStringKey(self)
    }
}
