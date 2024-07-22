//
//  EditTimerViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 21.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import FirebaseAuth

class EditTimerViewModel: ObservableObject {
    static var shared = EditTimerViewModel()
    
    @Published var isEditTimerSheet: Bool = false
    @Published var isSyncAnimation: Bool = false
    @Published var isSyncDoneAnimation: Bool = false
    
    @Published var selectedCountdown: CountdownTimer? = nil
    
    @Published var pickerChoose: [TimerType] = [TimerType.meal, TimerType.water]
    @Published var pickerActive: String = TimerType.water.rawValue

    let ftm = FirestoreTimerManager.shared
    
    var timerNameBinding: Binding<String> {
        Binding(
            get: { self.selectedCountdown?.name ?? ""  },
            set: { newName in
                if self.selectedCountdown != nil {
                    self.selectedCountdown?.name = newName
                }
            }
        )
    }
    
    var timerDurationBinding: Binding<Int> {
        Binding(
            get: { Int(self.selectedCountdown?.duration ?? 5) },
            set: { newValue in
                if self.selectedCountdown != nil {
                    self.selectedCountdown?.duration = newValue
                    self.selectedCountdown?.remainingDuration = newValue
                }
            }
        )
    }
    
    var timerTypeBinding: Binding<String> { // Two-way binding for the Picker
        Binding<String>(
            get: { self.selectedCountdown?.timerType ?? TimerType.meal.rawValue },
            set: { (self.selectedCountdown?.timerType = TimerType(rawValue: $0)?.rawValue ?? TimerType.meal.rawValue) }
        )
    }
    
    func openEditSheet(countdown: CountdownTimer) {
        isEditTimerSheet = true
        selectedCountdown = countdown
    }
    
    func closeEditSheet() {
        isEditTimerSheet = false
        selectedCountdown = nil
    }
    
    func resetTimer() {
        isEditTimerSheet = false
    }
    
    func saveEditTimer() {
        if let countdown = selectedCountdown {
            
            let updateTimer = CountdownTimer(
                id: countdown.id,
                userId: countdown.userId,
                name: countdown.name,
                duration: countdown.duration,
                startDate: countdown.startDate,
                endDate: countdown.endDate,
                timerState: countdown.timerState,
                timerType: countdown.timerType,
                remainingDuration: countdown.remainingDuration
            )
            
            ftm.editTimer(countdown: updateTimer)
        }
    }
}
