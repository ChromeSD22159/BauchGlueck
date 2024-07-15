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

    @State var listener: AuthStateDidChangeListenerHandle? = nil
    
    init() {
        self.user = Auth.auth().currentUser
        stateChangeListener()
    }
    
    func stateChangeListener() {
        listener = Auth.auth().addStateDidChangeListener { (auth, user) in
            self.user = user
        }
    }
    
    func removeStateListener() {
        guard let listener = listener else { return }
        Auth.auth().removeStateDidChangeListener(listener)
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

    func signIn(email: String, password: String, completion: @escaping (Bool, Error?) -> Void) {
        Auth.auth().signIn(withEmail: email, password: password) { authResult, error in
            if let error = error {
                print("Sign-in error: \(error.localizedDescription)")
                completion(false, error)
                return
            }
            
            guard let user = authResult?.user else {
                print("User is nil after sign-in")
                let userError = NSError(domain: "AuthError", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to sign in user"])
                completion(false, userError)
                return
            }
            
            // 3. Get ID token and save it
            user.getIDTokenForcingRefresh(true) { idToken, error in
                if let error = error {
                    print("ID token refresh error: \(error.localizedDescription)")
                    completion(false, error)
                    return
                }
                
                guard let idToken = idToken else {
                    print("ID token is nil")
                    let idTokenError = NSError(domain: "AuthError", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to retrieve ID token"])
                    completion(false, idTokenError)
                    return
                }
                
                ///
            }
            
            //UserDefaults.standard.set(idToken, forKey: "user_id_token")
            completion(true, nil)
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
    
    func fetchAppCheckToken() {
        AppCheck.appCheck().token(forcingRefresh: true) { token, error in
            if let error = error {
                print("Error fetching AppCheck token: \(error.localizedDescription)")
                return
            }
            
            guard let token = token else {
                print("AppCheck token is nil")
                return
            }
            
            print("Received AppCheck token: \(token.token)")
        }
    }
}

// TODO: REFACTOR
enum LoginNav {
    case login
    case signUp
    case logged
}
