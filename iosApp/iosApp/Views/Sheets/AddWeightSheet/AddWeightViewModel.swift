//
//  AddWeightViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

class AddWeightViewModel: ObservableObject {
    @Published var isAddWeightSheet: Bool = false
    @Published var currentWeight: Double = 70.0
    @Published var targetWeight: Double = 100.0
    
    private var healthManager = HealthManager.shared
    private var incrementValue = 0.1
    let weights = Array(stride(from: 50.0, through: 150.0, by: 10.0))
    
    func increase() {
        currentWeight += incrementValue
    }
    
    func decrease() {
        if currentWeight > 0 {
            currentWeight -= incrementValue
        }
    }
    
    func saveWeightToHealth() {
        if currentWeight <= 0 { return }
        
        healthManager.saveWeight(weight: currentWeight, date: Date())
    }
    
    func wait(forSeconds: Double) async -> ()? {
        let nanoseconds = UInt64(forSeconds * 1_000_000_000)
        return try? await Task.sleep(nanoseconds: nanoseconds)
    }
    
    func resetWeightAmount() {
        guard let data = HealthManager.shared.getLastWeightData() else {
            if let user = FirebaseAuthManager.shared.userProfile {
                currentWeight = user.startWeight
            }
            return
        }
        
        currentWeight = data.weight
    }
}
