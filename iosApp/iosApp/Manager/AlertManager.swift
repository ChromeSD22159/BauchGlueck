//
//  AlertManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 15.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

class AlertManager: ObservableObject {
    @Published var presentAlert = false
    
    @Published var message = ""
    
    func openAlert(_ msg: String) {
        presentAlert = true
        message = msg
    }
    
    func closeAlert() {
        presentAlert = false
        message = ""
    }
}
