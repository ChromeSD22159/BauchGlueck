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
    
    @Published var selectedCountdown: CountdownTimer? = nil
    
    @Published var pickerChoose: [TimerType] = [TimerType.meal, TimerType.water]
    @Published var pickerActive: String = TimerType.water.rawValue

    var timerNameBinding: Binding<String> {
        Binding(
            get: { self.selectedCountdown?.name ?? "" },
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
    
    func initTimer(user: User) {
        let defaulTime = 30 * 60
        self.selectedCountdown =  CountdownTimer(
            id: UUID().uuidString,
            userId: user.uid,
            name: "",
            duration: defaulTime,
            startDate: nil,
            endDate: nil,
            timerState: TimerState.notRunning.rawValue,
            timerType: TimerType.meal.rawValue,
            remainingDuration: defaulTime
        )
    }
    
    func saveTimer(complete: @escaping(CountdownTimer?, Bool) -> Void) {
        if let countdown = selectedCountdown {
            FirestoreTimerManager.shared.saveTimer(countdown: countdown, complete: { timer , bool in
                complete(timer, bool)
            })
        }
    }
}
