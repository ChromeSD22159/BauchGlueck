//
//  AddWaterViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

class AddWaterViewModel: ObservableObject {
    @Published var isAddWaterSheet: Bool = false
    
    @Published var drinkAmount: Int = 0
    
    private var healthManager = HealthManager.shared
    private var value = 100
    
    var intakePerDay: Int {
        guard let intakePerDay = FirebaseAuthManager.shared.userProfile?.waterDayIntake else { return 2000 }
        return Int(intakePerDay)
    }
    
    var trimPercent: CGFloat {
        CGFloat(min(Double(drinkAmount) / Double(intakePerDay), 1.0))
    }
    
    var intakePercentPerDay: Int {
        Int(Double(drinkAmount) / Double(intakePerDay) * 100)
    }
    
    func increase() {
        drinkAmount += value
    }
    
    func decrease() {
        if drinkAmount > 0 {
            drinkAmount -= value
        }
    }
    
    func saveWaterToHealth() {
        if drinkAmount <= 0 { return }
        healthManager.saveWater(intakeLiter: drinkAmount.milliToLiter, date: Date())
    }
    
    func wait(forSeconds: Double) async -> ()? {
        let nanoseconds = UInt64(forSeconds * 1_000_000_000)
        return try? await Task.sleep(nanoseconds: nanoseconds)
    }
    
    func resetDrinkAmount() {
        drinkAmount = 0
    }
}
