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

    var greeting: LocalizedStringKey {
        let cal = Calendar.current
        let hour = cal.component(.hour, from: Date())
        let user = "\(authManager.userProfile?.firstName ?? "Unknown")!"
        
        let formattedString: String
        
        switch hour {
            case 2 ... 11 : formattedString = String(format: NSLocalizedString("Good morning, %@", comment: ""), user)
            case 11 ... 18 : formattedString = String(format: NSLocalizedString("Hello, %@", comment: ""), user)
            case 18 ... 22 : formattedString = String(format: NSLocalizedString("Good evening, %@", comment: ""), user)
            default: formattedString = String(format: NSLocalizedString("Hello, %@", comment: ""), user)
        }
        
        return LocalizedStringKey(formattedString)
    }
    
    var timeSinceSurgery: LocalizedStringKey {
        guard let surgeryDate = authManager.userProfile?.surgeryDate else { return "No surgery date" }

        let calendar = Calendar.current
        let today = Date()
        
        let components = calendar.dateComponents([.year, .month, .day], from: surgeryDate, to: today)

        let years: Int = abs(components.year ?? 0)
        let months: Int = abs(components.month ?? 0)
        let days: Int = abs(components.day ?? 0)

        if surgeryDate < today {
            let formattedString = String(format: NSLocalizedString("%d years, %d months, %d days since you restarted.", comment: ""), years, months, days)
            return LocalizedStringKey(formattedString)
        } else {
            let formattedString: String
            
            if years > 0 {
                formattedString = String(format: NSLocalizedString("Only %d years, %d months, %d days until you restart.", comment: ""), years, months, days)
            } else if months > 0 {
                formattedString = String(format: NSLocalizedString("Only %d months, %d days until you restart.", comment: ""), months, days)
            } else {
                formattedString = String(format: NSLocalizedString("Only %d days until you restart.", comment: ""), days)
            }
            
            return LocalizedStringKey(formattedString)
        }
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
    
    var startWeigtBinding: Binding<Double> {
        Binding(
            get: { self.authManager.userProfile?.startWeight ?? 100 },
            set: { newValue in
                self.authManager.userProfile?.startWeight = newValue
                //self.updateUserProfileUser()
            }
        )
    }
    
    var waterIntakeBinding: Binding<Double> {
        Binding(
            get: { self.authManager.userProfile?.waterIntake ?? 200 },
            set: { newValue in
                self.authManager.userProfile?.waterIntake = newValue
                //self.updateUserProfileUser()
            }
        )
    }
    
    var waterDayIntakeBinding: Binding<Double> {
        Binding(
            get: { self.authManager.userProfile?.waterDayIntake ?? 2000 },
            set: { newValue in
                self.authManager.userProfile?.waterDayIntake = newValue
                //self.updateUserProfileUser()
            }
        )
    }
    
    var mainMealsBinding: Binding<Int> {
        Binding(
            get: { self.authManager.userProfile?.mainMeals ?? 3 },
            set: { newValue in
                self.authManager.userProfile?.mainMeals = newValue
            }
        )
    }
    
    var betweenMealsBinding: Binding<Int> {
        Binding(
            get: { self.authManager.userProfile?.betweenMeals ?? 3 },
            set: { newValue in
                self.authManager.userProfile?.betweenMeals = newValue
            }
        )
    }
}



extension String {
    func translate(with values: [CVarArg]) -> String {
           return String(format: NSLocalizedString(self, comment: ""), arguments: values)
       }
}
