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
import AuthenticationServices

class LoginViewModel: ObservableObject {
    @Published var email = ""
    @Published var password = ""
    @Published var errorMessage = ""
    
    func login(complete: @escaping (Error?) -> Void) {
        guard !email.isEmpty, !password.isEmpty else {
            errorMessage = "Please enter email and password."
            return
        }
        
        FirebaseAuthManager.shared.signIn(email: email, password: password, complete: { error in
            complete(error)
        })
    }
}
