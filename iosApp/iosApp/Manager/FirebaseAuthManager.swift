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
    
    @Published var user: User? = nil
    
    @Published var nav: LoginNav = .login
    
    init() {
        self.user = Auth.auth().currentUser
        stateChangeListener()
    }
    
    let userError = NSError(domain: "AuthError", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to sign in user"])
    
    func stateChangeListener() {
        Auth.auth().addStateDidChangeListener { (auth, user) in
            if (user != nil) {
                self.user = user
                self.nav = .logged
            } else {
                self.nav = .login
            }
        }
    }

    func signUp(email: String, password: String, complete: @escaping (Error?) -> Void ) {
        Auth.auth().createUser(withEmail: email, password: password) { authResult, error in
            if ((authResult?.user) != nil) {
                self.signOut()
                complete(nil)
            }
            
            if (error != nil) {
                complete(error)
            }
        }
    }

    func signIn(email: String, password: String, complete: @escaping (Error?) -> Void) {
        Auth.auth().signIn(withEmail: email, password: password) { authResult, error in
            if let error = error {
                return complete(error)
            }
            
            guard let user = authResult?.user else {
                return complete(self.userError)
            }

            self.user = user
            
            self.nav = .logged
            
            complete(nil)
        }
    }

    func signOut() {
        do {
            try Auth.auth().signOut()
            self.user = nil
            print("Sign out")
        } catch {
          print("Sign out error")
        }
    }
}

// TODO: REFACTOR
enum LoginNav {
    case login
    case signUp
    case logged
}
