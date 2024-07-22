//
//  HealtManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 22.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import HealthKit

class HealthManager: ObservableObject {
    static var shared = HealthManager()
    
    @Published var waterList: [WaterIntake] = []
    @Published var weightList: [WeightRecord] = []
    @Published var days = 7
    
    let healthStore = HKHealthStore()

    // Typen der Daten, auf die die App zugreifen wird
    let allTypes = Set([
        HKObjectType.quantityType(forIdentifier: .dietaryWater)!,
        HKObjectType.quantityType(forIdentifier: .bodyMass)!
    ])

    
    
    // Initialisierung und Berechtigungen anfordern
    init() {
        requestAuthorization()

        fetchLists(days: days)
    }
    
    func fetchLists(days: Int) {
        fetchWeightData(days: days, completion: { weightList in
            self.weightList = weightList
        })
        
        fetchWaterIntakeData(days: days, completion: { waterList in
            self.waterList = waterList
        })
    }

    func requestAuthorization() {
        healthStore.requestAuthorization(toShare: allTypes, read: allTypes) { success, error in
            if !success {
                print("Authorization failed: \(String(describing: error?.localizedDescription))")
            }
        }
    }

    // Gewicht speichern
    func saveWeight(weight: Double, date: Date) {
        let weightType = HKQuantityType.quantityType(forIdentifier: .bodyMass)!
        let weightQuantity = HKQuantity(unit: HKUnit.gramUnit(with: .kilo), doubleValue: weight)
        let weightSample = HKQuantitySample(type: weightType, quantity: weightQuantity, start: date, end: date)

        healthStore.save(weightSample) { success, error in
            if success {
                print("Weight saved successfully.")
                
                self.fetchLists(days: self.days)
                self.objectWillChange.send()
            } else {
                print("Failed to save weight: \(String(describing: error?.localizedDescription))")
            }
        }
    }
    
    // Gewicht abrufen
    func fetchWeightData(days: Int, completion: @escaping ([WeightRecord]) -> Void) {
        // Stelle sicher, dass HealthKit verfügbar ist
        guard HKHealthStore.isHealthDataAvailable() else {
            completion([])
            return
        }

        let weightType = HKObjectType.quantityType(forIdentifier: .bodyMass)!
        
        // Berechne die Start- und Enddaten für die Abfragen
        let calendar = Calendar.current
        let endDate = Date()
        let startDate = calendar.date(byAdding: .day, value: -days + 1, to: endDate)!
        
        var weightRecords = [WeightRecord]()
        let dispatchGroup = DispatchGroup()
        
        for dayOffset in 0..<days {
            let queryStartDate = calendar.date(byAdding: .day, value: -dayOffset, to: endDate)!
            let queryEndDate = calendar.date(byAdding: .day, value: -dayOffset + 1, to: endDate)!
            
            let predicate = HKQuery.predicateForSamples(withStart: queryStartDate, end: queryEndDate, options: .strictStartDate)
            let query = HKStatisticsQuery(quantityType: weightType, quantitySamplePredicate: predicate, options: .discreteAverage) { _, result, error in
                defer {
                    dispatchGroup.leave()
                }
                
                if let error = error {
                    print("Error fetching weight data for \(queryStartDate): \(error.localizedDescription)")
                    weightRecords.append(WeightRecord(weight: 0.0, date: queryStartDate))
                    return
                }

                let averageWeight = result?.averageQuantity()?.doubleValue(for: HKUnit.gramUnit(with: .kilo)) ?? 0.0
                weightRecords.append(WeightRecord(weight: averageWeight, date: queryStartDate))
            }
            
            dispatchGroup.enter()
            healthStore.execute(query)
        }
        
        dispatchGroup.notify(queue: .main) {
            // Sortiere die Daten nach Datum (optional, falls gewünscht)
            let sortedWeightRecords = weightRecords.sorted { $0.date < $1.date }
            completion(sortedWeightRecords)
        }
    }
    
    // Getränke speichern
    func saveWater(intakeLiter: Double, date: Date) {
        let waterType = HKQuantityType.quantityType(forIdentifier: .dietaryWater)!
        let waterQuantity = HKQuantity(unit: HKUnit.liter(), doubleValue: intakeLiter)
        let waterSample = HKQuantitySample(type: waterType, quantity: waterQuantity, start: date, end: date)

        healthStore.save(waterSample) { success, error in
            if success {
                print("Water intake saved successfully.")
                
                self.fetchLists(days: self.days)
                self.objectWillChange.send()
                
            } else {
                print("Failed to save water intake: \(String(describing: error?.localizedDescription))")
            }
        }
    }
    
    func fetchWaterIntakeData(days: Int, completion: @escaping ([WaterIntake]) -> Void) {
        // Stellen Sie sicher, dass HealthKit verfügbar ist
        guard HKHealthStore.isHealthDataAvailable() else {
            completion([])
            return
        }

        let waterType = HKObjectType.quantityType(forIdentifier: .dietaryWater)!
        
        // Berechne die Start- und Enddaten für die Abfragen
        let calendar = Calendar.current
        let endDate = Date()
        var startDate = calendar.date(byAdding: .day, value: -days + 1, to: endDate)!
        
        var waterIntakes = [WaterIntake]()
        let dispatchGroup = DispatchGroup()
        
        // Hilfsfunktion zum Hinzufügen von WaterIntake-Daten
        func addWaterIntake(for date: Date, intake: Double) {
            waterIntakes.append(WaterIntake(intake: intake, date: date))
        }
        
        for dayOffset in 0..<days {
            let queryStartDate = calendar.date(byAdding: .day, value: -dayOffset, to: endDate)!
            let queryEndDate = calendar.date(byAdding: .day, value: -dayOffset + 1, to: endDate)!
            
            let predicate = HKQuery.predicateForSamples(withStart: queryStartDate, end: queryEndDate, options: .strictStartDate)
            let query = HKStatisticsQuery(quantityType: waterType, quantitySamplePredicate: predicate, options: .cumulativeSum) { _, result, error in
                defer {
                    dispatchGroup.leave()
                }
                
                if let error = error {
                    addWaterIntake(for: queryStartDate, intake: 0.0)
                    return
                }

                let totalWaterIntake = result?.sumQuantity()?.doubleValue(for: .liter()) ?? 0.0
                addWaterIntake(for: queryStartDate, intake: totalWaterIntake)
            }
            
            dispatchGroup.enter()
            healthStore.execute(query)
        }
        
        dispatchGroup.notify(queue: .main) {
            // Sortiere die Daten nach Datum (optional, falls gewünscht)
            let sortedWaterIntakes = waterIntakes.sorted { $0.date < $1.date }
            print(sortedWaterIntakes)
            completion(sortedWaterIntakes)
        }
    }
}
