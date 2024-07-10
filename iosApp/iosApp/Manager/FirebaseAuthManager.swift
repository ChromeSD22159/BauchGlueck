//
//  FirebaseAuthManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth
import Firebase
import SwiftUI

class FirebaseAuthManager: ObservableObject {
    
    @AppStorage("Logged") var isLoggedIn = false
    
    @Published var user: User? = nil
    
    @Published var nav: LoginNav = .login

    init() {
        self.user = Auth.auth().currentUser
        Auth.auth().addStateDidChangeListener { (auth, user) in
            self.user = user
        }
    }

    func signUp(email: String, password: String, completion: @escaping (User?, Error?) -> Void) {
        Auth.auth().createUser(withEmail: email, password: password) { authResult, error in
            if let error = error {
                self.isLoggedIn = false
                completion(nil,error)
            } else if let user = authResult?.user {
                self.isLoggedIn = true
                completion(user, nil)
            }
        }
    }

    func signIn(email: String, password: String) {
        // 1. Fetch App Check token
        AppCheck.appCheck().token(forcingRefresh: false) { token, error in
            if let error = error {
                print(error.localizedDescription)
                return
            }
            
            guard let token = token else {
                print(token ?? "")
                return
            }
            
            Auth.auth().signIn(withEmail: email, password: password) { authResult, error in
                if let error = error {
                    print(error.localizedDescription)
                } else if let user = authResult?.user {
                    user.getIDTokenForcingRefresh(true) { idToken, error in
                        if let error = error {// Handle ID token refresh error
                        } else if let idToken = idToken {
                            UserDefaults.standard.set(idToken, forKey: "user_id_token")
                        }
                    }
                }
            }
        }
    }

    func signOut() {
        do {
            try Auth.auth().signOut()
            self.user = nil
            self.isLoggedIn = false
            print("Sign out")
        } catch {
          print("Sign out error")
        }
    }
    
    func isValidEmail(_ email: String) -> Bool {
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        let emailPred = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        return emailPred.evaluate(with: email)
    }
      
    func isValidPassword(_ password: String) -> Bool {
        let minPasswordLength = 6
        return password.count >= minPasswordLength
    }
}

// TODO: REFACTOR
enum LoginNav {
    case login
    case signUp
    case logged
}
