//
//  File.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth
import FirebaseFirestore
import SwiftUI

class AddTimerViewModel: ObservableObject {
    @Published var isAddTimerSheet: Bool = false
    @Published var isSyncAnimation: Bool = false
    @Published var isSyncDoneAnimation: Bool = false
    
    @Published var timerList: [Timer] = []
    
    @Published var selectedTimer: Timer? = nil
    
    @Published var pickerChoose: [TimerType] = [TimerType.meal, TimerType.water]
    @Published var pickerActive: TimerType = TimerType.water 
    
    var timerNameBinding: Binding<String> {
        Binding(
            get: { self.selectedTimer?.name ?? "" },
            set: { newName in
                if self.selectedTimer != nil {
                    self.selectedTimer?.name = newName
                }
            }
        )
    }
    
    var timerDurationBinding: Binding<Int> {
        Binding(
            get: { Int(self.selectedTimer?.duration ?? 5) },
            set: { newValue in
                if self.selectedTimer != nil {
                    self.selectedTimer?.duration = newValue
                }
            }
        )
    }
    
    var timerTypeBinding: Binding<String> {
        Binding(
            get: { self.selectedTimer?.timerType ?? AddTimerViewModel.TimerType.meal.rawValue },
            set: { newType in
                if self.selectedTimer != nil {
                    self.selectedTimer?.timerType = newType
                }
            }
        )
    }
    
    func initTimer(user: User) {
        self.selectedTimer = Timer(
            userId: user.uid,
            name: "",
            timerType: TimerType.meal.rawValue,
            duration: 30,
            timerState: TimerState.notRunning.rawValue
        )
    }
    
    struct Timer: Identifiable {
        let id: String = UUID().uuidString
        let userId: String
        var name: String
        var timerType: String
        var duration: Int
        var startDate: Date?
        var timerState: String
        var remainingDuration: Int?
    }

    enum TimerType: String {
        case meal = "Meal"
        case water = "Water"
    }
    
    enum TimerState: String {
        case running = "Running"
        case notRunning = "Not running"
        case pause = "Pause"
    }
    
    func saveTimer() -> Bool {
        var result = false
        
        guard let timer = selectedTimer else { return result }
        
        guard timer.name.count >= 3 else { return result }
        
        let db = Firestore.firestore()

        let timerRef = db.collection("timers").document(timer.id)
 
        let timerData: [String: Any] = [
            "id": timer.id,
            "userId": timer.userId,
            "name": timer.name,
            "timerType": timer.timerType,
            "duration": timer.duration,
            "startDate": timer.startDate ?? Date(),
            "timerState": timer.timerState,
            "remainingDuration": timer.remainingDuration ?? Date(),
        ]
        
        timerRef.setData(timerData) { error in
            if let error = error {
                print("Error saving user profile: \(error.localizedDescription)")
            } else {
                print("Timer saved successfully!")
                print(timerData)
                result = true
            }
        }
        
        return result
    }
}



