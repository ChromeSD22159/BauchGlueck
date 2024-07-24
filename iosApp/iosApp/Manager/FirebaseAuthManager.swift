//
//  FirebaseAuthManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import FirebaseFirestore
import FirebaseStorage
import Firebase
import FirebaseAuth
import SwiftUI
import AuthenticationServices

class FirebaseAuthManager: ObservableObject {
    
    static var shared = FirebaseAuthManager()
    
    @Published var user: User? = nil
    
    @Published var userProfile: UserProfile? = nil
    
    @Published var userProfileImage = UIImage()
    
    @Published var nav: LoginNav = .login
    
    @Published var showSyn = false
    
    let db = Firestore.firestore()
    let storage = Storage.storage()
    let appName = "de.frederikkohler.bauchGlueck"
    
    init() {
        let _ = Auth.auth().currentUser
        stateChangeListener()
    }
    
    var initials: String {
        guard
            let firstName = userProfile?.firstName, !firstName.isEmpty,
            let lastName = userProfile?.lastName, !lastName.isEmpty
        else { return "" }

        let firstInitial = String(firstName.prefix(1)).uppercased()
        let lastInitial = String(lastName.prefix(1)).uppercased()
        return firstInitial + lastInitial
    }
    
    let userError = NSError(domain: "AuthError", code: -1, userInfo: [NSLocalizedDescriptionKey: "Failed to sign in user"])
    
    func stateChangeListener() {
        self.showSyn = true
        Auth.auth().addStateDidChangeListener { (auth, user) in
            if (user != nil) {
                self.user = user
                self.fetchUserProfile(completion: { user in
                    if let user = user {
                        self.userProfile = user
                        
                        if let profileImageURL = user.profileImageURL {
                            self.downloadProfileImage(imageURL: profileImageURL, completion: { result in
                                switch result {
                                    case .success(let image):  self.userProfileImage = image
                                    case .failure(let error): print("Error downloading profile image: \(error.localizedDescription)")
                                }
                                
                                self.showSyn = false
                            })
                        }
                        
                        
                    } else {
                        print("Failed to load user profile")
                    }
                })
                self.nav = .logged
                print("logged")
            } else {
                self.nav = .login
            }
        }
    }

    func signUp(email: String, password: String, complete: @escaping (Error?, User?) -> Void ) {
        Auth.auth().createUser(withEmail: email, password: password) { authResult, error in
            if ((authResult?.user) != nil) {
                complete(nil, authResult?.user)
            }
            
            if (error != nil) {
                complete(error, nil)
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
    
    func saveUserProfile() {
        
        guard let userProfil = userProfile else { return }
        
        let userRef = db.collection("users").document(userProfil.uid)
        
        let userData: [String: Any] = [
            "firstName": userProfil.firstName,
            "lastName": userProfil.lastName,
            "email": userProfil.email,
            "surgeryDate": userProfil.surgeryDate,
            "mainMeals": userProfil.mainMeals,
            "betweenMeals": userProfil.betweenMeals,
            "profileImageURL": userProfil.profileImageURL ?? "",
            "startWeight": userProfil.startWeight,
            "waterIntake": userProfil.waterIntake,
            "waterDayIntake": userProfil.waterDayIntake
        ]

        // Setze die Daten in Firestore
        userRef.setData(userData) { error in
            if let error = error {
                print("Error saving user profile: \(error.localizedDescription)")
            } else {
                print("User profile saved successfully!")
            }
        }
    }
    
    private func fetchUserProfile(completion: @escaping (UserProfile?) -> Void) {
        guard let user = self.user else { return }
        let userRef = db.collection("users").document(user.uid)

        userRef.getDocument { (document, error) in
            guard let document = document, document.exists, let data = document.data() else {
                print("User profile not found or error fetching: \(error?.localizedDescription ?? "")")
                completion(nil)
                return
            }

            let userProfile = UserProfile(
                uid: user.uid,
                firstName: data["firstName"] as? String ?? "",
                lastName: data["lastName"] as? String ?? "",
                email: data["email"] as? String ?? "",
                surgeryDate: (data["surgeryDate"] as? Timestamp)?.dateValue() ?? Date(),
                mainMeals: data["mainMeals"] as? Int ?? 3,
                betweenMeals: data["betweenMeals"] as? Int ?? 3,
                profileImageURL: data["profileImageURL"] as? String,
                startWeight: data["startWeight"] as? Double ?? 100,
                waterIntake:data["waterIntake"] as? Double ?? 100,
                waterDayIntake:data["waterDayIntake"] as? Double ?? 2000
            )
            
            completion(userProfile)
        }
    }
    
    func uploadAndSaveProfileImage(uiImage: UIImage, completion: @escaping (Result<Void, Error>) -> Void) {
        guard let user = self.user else {
            completion(.failure(NSError(domain: "AuthError", code: -2, userInfo: [NSLocalizedDescriptionKey: "User not logged in"])))
            return
        }
        
        let imageName = "\(user.uid)_profile_image.jpg"
        let storageRef = storage.reference(withPath: "profile_images/\(imageName)")
        let metadata = StorageMetadata()
        metadata.contentType = "image/jpeg"

        guard let scaledImage = uiImage.resizedAndCropped(to: CGSize(width: 512, height: 512)), let imageData = scaledImage.jpegData(compressionQuality: 0.5) else {
            completion(.failure(NSError(domain: "ImageError", code: -3, userInfo: [NSLocalizedDescriptionKey: "Image compression or resizing failed"])))
            return
        }
        
        storageRef.putData(imageData, metadata: metadata) { (metadata, error) in
            if let error = error {
                completion(.failure(error))
            } else {
                storageRef.downloadURL { (url, error) in
                    guard let downloadURL = url else {
                        completion(.failure(error!))
                        return
                    }
                    self.userProfile?.profileImageURL = downloadURL.absoluteString
                    self.saveUserProfile()
                    self.userProfileImage = scaledImage
                    completion(.success(()))
                }
            }
        }
    }
    
    func downloadProfileImage(imageURL: String, completion: @escaping (Result<UIImage, Error>) -> Void) {
        guard let url = URL(string: imageURL), url.scheme == "https" else {
               completion(.failure(NSError(domain: "DownloadError", code: 1, userInfo: [NSLocalizedDescriptionKey: "Invalid image URL"])))
               return
           }
           
        let storageRef = storage.reference(forURL: imageURL)
        
        storageRef.getData(maxSize: 1 * 1024 * 1024) { (data, error) in // 1 MB max size
            if let error = error {
                completion(.failure(error))
            } else {
                if let imageData = data, let image = UIImage(data: imageData) {
                    completion(.success(image))
                } else {
                    completion(.failure(NSError(domain: "DownloadError", code: 0, userInfo: nil)))
                }
            }
        }
    }
}
