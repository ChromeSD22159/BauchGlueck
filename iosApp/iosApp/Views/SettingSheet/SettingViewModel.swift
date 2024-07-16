//
//  SettingViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 16.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

class SettingViewModel: ObservableObject {
    var authManager: FirebaseAuthManager
    
    init(authManager: FirebaseAuthManager) {
        self.authManager = authManager
    }
    
    @FocusState var isFocused: Bool
    
    @Published var showImageSheet = false
    
    func updateUserProfileUser() {
        authManager.saveUserProfile()
    }
    
    var timeSinceSurgery: String {
        guard let surgeryDate = authManager.userProfile?.surgeryDate else { return "No surgery date" }

        if surgeryDate < Date() {
            return calculateTimeSinceSurgery(surgeryDate: surgeryDate) + " seit deinem Neustart."
        } else {
            return calculateCountownSurgery(surgeryDate: surgeryDate)
        }
    }
    
    var greeting: String {
        let cal = Calendar.current
        let hour = cal.component(.hour, from: Date())
        let user = (authManager.userProfile?.firstName ?? "") + " " + (authManager.userProfile?.lastName ?? "")
        switch hour {
            case 2 ... 11 : return "Guten Morgen, \(user)"
            case 11 ... 18 : return "Guten Tag, \(user)"
            case 18 ... 22 : return "Guten Abend, \(user)"
            default: return "Hallo, \(user)"
        }
    }
    
    private func calculateTimeSinceSurgery(surgeryDate: Date) -> String {
        let calendar = Calendar.current
        let today = Date()
        
        let components = calendar.dateComponents([.year, .month, .day], from: surgeryDate, to: today)

        let years = components.year ?? 0
        let months = components.month ?? 0
        let days = components.day ?? 0
        
        return "\(years) Jahre, \(months) Monate, \(days) Tage"
    }
    
    private func calculateCountownSurgery(surgeryDate: Date) -> String {
        let calendar = Calendar.current
        let today = Date()
        
        let components = calendar.dateComponents([.month, .day], from: surgeryDate, to: today)
        
        let months = abs(components.month ?? 0)
        let days = abs(components.day ?? 0)
        
        return "Nur noch \(months) Monate, \(days) Tage"
    }
    
    var firstNameBinding: Binding<String> {
        Binding(
            get: { self.authManager.userProfile?.firstName ?? "" },
            set: { newValue in
                self.authManager.userProfile?.firstName = newValue
                //self.updateUserProfileUser()
            }
        )
    }
    
    var lastNameBinding: Binding<String> {
        Binding(
            get: { self.authManager.userProfile?.lastName ?? "" },
            set: { newValue in
                self.authManager.userProfile?.lastName = newValue
                //self.updateUserProfileUser()
            }
        )
    }
    
    var surgeryDateBinding: Binding<Date> {
        Binding(
            get: { self.authManager.userProfile?.surgeryDate ?? Date() },
            set: { newValue in
                self.authManager.userProfile?.surgeryDate = newValue
                //self.updateUserProfileUser()
            }
        )
    }
    
    var mainMeals: Binding<Int> {
        Binding(
            get: { self.authManager.userProfile?.mainMeals ?? 3 },
            set: { newValue in
                self.authManager.userProfile?.mainMeals = newValue
            }
        )
    }
    
    var betweenMeals: Binding<Int> {
        Binding(
            get: { self.authManager.userProfile?.betweenMeals ?? 3 },
            set: { newValue in
                self.authManager.userProfile?.betweenMeals = newValue
            }
        )
    }
}
