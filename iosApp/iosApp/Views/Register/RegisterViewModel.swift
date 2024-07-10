//
//  RegisterViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth

class RegisterViewModel: ObservableObject {
    
    @Published var firstName: String = "Frederik"
    @Published var lastName: String = "Kohler"
    @Published var email: String = "info@frederikkohler.de"
    @Published var password: String = "Frederik@Kohler"
    @Published var passwordVerify: String = "Frederik@Kohler"
    @Published var surgeryDate: Date = Date()
    @Published var profilePicture: String = ""
    
    @Published var errorMessage: String = ""
    @Published var isAuthenticated: Bool = false
        
    private var authManager = FirebaseAuthManager()
    
    func signUp(complete: @escaping(Bool) -> Void) {
        let res = false
        authManager.signUp(email: email, password: password, completion: { user , error in
            if (error == nil) {
                complete(true)
                print("User signs up successfully")
            } else {
                complete(false)
                
            }
        })
        
    }
    
    func isUserLoggedIn() -> Bool {
      return Auth.auth().currentUser != nil
    }
    
    private func validatePasswords() -> Bool {
       guard password == passwordVerify else {
           print("Passwords stimmen nicht überein")
           return false
       }

       let passwordRegex = NSPredicate(format: "SELF MATCHES %@", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$")
       return passwordRegex.evaluate(with: password)
   }
}
