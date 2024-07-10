//
//  LoginViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth
import SwiftUI

class LoginViewModel: ObservableObject {
    @Published var email = "info@frederikkohler.de"
    @Published var password = "Fr3d3rik@Kohler"
    @Published var errorMessage = ""
    @AppStorage("Logged") var isLoggedIn = false
    
    init() {
        Auth.auth().addStateDidChangeListener { _, user in
            self.isLoggedIn = (user != nil)
        }
    }

    func login() {
        // Einfache Validierung (hier könntest du eine gründlichere Validierung hinzufügen)
        guard !email.isEmpty, !password.isEmpty else {
            errorMessage = "Please enter email and password."
            return
        }
        
        FirebaseAuthManager().signIn(email: email, password: password)
    }
}
