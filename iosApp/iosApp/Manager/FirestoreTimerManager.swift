//
//  FirestoreManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 18.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import Firebase

class FirestoreTimerManager: ObservableObject {
    static let shared = FirestoreTimerManager()

    private let db = Firestore.firestore()
    
    @Published var timerList: [CountdownTimer] = []
    
    func initialize(loadLokal: Bool) {
        guard let userId = Auth.auth().currentUser?.uid else { return } // Get current user
        
        /*
        if loadLokal {
            db.disableNetwork()
            print("initialize lokal")
        } else {
            db.enableNetwork()
            print("initialize firebase")
        }
         */
        
        db.collection("timers")
            .whereField("userId", isEqualTo: userId)
            .addSnapshotListener { [weak self] querySnapshot, error in
                guard let documents = querySnapshot?.documents else {
                    print("Error fetching timers: \(error?.localizedDescription ?? "Unknown error")")
                    return
                }
                
                let list = documents.compactMap { try? $0.data(as: CountdownTimer.self) }
                
                print(list)
                
                FirestoreTimerManager.shared.timerList = list
                print("initialized \(self?.timerList.count ?? -1)")
            }
    }
    
    func synronize() {
        
        guard let userId = Auth.auth().currentUser?.uid else { return }
        
        db.collection("timers")
            .whereField("userId", isEqualTo: userId)
            .addSnapshotListener { [weak self] querySnapshot, error in
                guard let documents = querySnapshot?.documents else {
                    print("Error fetching timers: \(error?.localizedDescription ?? "Unknown error")")
                    return
                }
                
                self?.timerList = documents.compactMap { try? $0.data(as: CountdownTimer.self) }
                
                print("synronized \(self?.timerList.count ?? -1)")
                self?.objectWillChange.send() // Update the UI
            }
    }

    private func fetchTimers() {
        guard let userId = Auth.auth().currentUser?.uid else { return } // Get current user
        db.collection("timers")
            .whereField("userId", isEqualTo: userId)
            .addSnapshotListener { [weak self] querySnapshot, error in
                guard let documents = querySnapshot?.documents else {
                    print("Error fetching timers: \(error?.localizedDescription ?? "Unknown error")")
                    return
                }
                self?.timerList = documents.compactMap { try? $0.data(as: CountdownTimer.self) }
            }
    }

    func saveTimer(countdown: CountdownTimer, complete: @escaping (CountdownTimer?, Bool) -> Void) {
        do {
            guard let userId = Auth.auth().currentUser?.uid else { return }
            var updatedCountdown = countdown
            updatedCountdown.userId = userId
            try Firestore.firestore().collection("timers").document(countdown.id).setData(from: updatedCountdown)
            complete(updatedCountdown, true)
        } catch {
            print("Error saving timer: \(error.localizedDescription)")
            complete(nil, false)
        }
    }

    func editTimer(countdown: CountdownTimer) {
        guard let userId = Auth.auth().currentUser?.uid else {
            print("No authenticated user")
            return
        }
        
        var updatedCountdown = countdown
        updatedCountdown.userId = userId

        let timerRef = db.collection("timers").document(countdown.id)
        
        if let index = timerList.firstIndex(where: { $0.id == countdown.id }) {
            timerList[index] = countdown
            objectWillChange.send() 
        }
        
        do {
                        
            try timerRef.setData(from: updatedCountdown, merge: true) { error in
                if let error = error {
                    print("Error updating timer: \(error.localizedDescription)")
                } else {
                    print("Timer updated successfully!")
                    
                    if let encoded = try? JSONEncoder().encode(updatedCountdown) {
                        UserDefaults.standard.set(encoded, forKey: updatedCountdown.id)
                        self.objectWillChange.send()
                    }
                }
            }
            
        } catch {
            print("Error updating timer: \(error.localizedDescription)")
        }
    }
    
    func deleteTimer(countdown: CountdownTimer) {
        Firestore.firestore().collection("timers").document(countdown.id).delete { error in
            if let error = error {
                print("Error deleting timer: \(error.localizedDescription)")
            } else {
                if let index = self.timerList.firstIndex(where: { $0.id == countdown.id }) {
                    self.timerList.remove(at: index)
                }
            }
        }
    }
    
    func saveCountdownToUserDefaults(countdown: CountdownTimer) {
        
        let countCountdownUserDefaultsState = CountdownUserDefaultsState(
            id: countdown.id,
            state: countdown.timerState,
            start: countdown.startDate,
            end: countdown.endDate
        )
        
        if let encoded = try? JSONEncoder().encode(countCountdownUserDefaultsState) {
            UserDefaults.standard.set(encoded, forKey: countCountdownUserDefaultsState.id)
        }
    }
    
    func removeCountdownFromUserDefaults(countdown: CountdownTimer) {
        UserDefaults.standard.removeObject(forKey: countdown.id)
    }
        
    func loadCountdownFromUserDefaults(countdown: CountdownTimer) -> CountdownTimer? {
        let defaults = UserDefaults.standard
        
        if let savedCountdown = defaults.object(forKey: countdown.id) as? Data {
            if let loadedCountdown = try? JSONDecoder().decode(CountdownUserDefaultsState.self, from: savedCountdown) {
                return CountdownTimer (
                    id: countdown.id,
                    userId: countdown.userId,
                    name: countdown.name,
                    duration: countdown.duration,
                    startDate: loadedCountdown.start,
                    endDate: loadedCountdown.end,
                    timerState: loadedCountdown.state,
                    timerType: countdown.timerType,
                    remainingDuration: countdown.remainingDuration
                )
            }
        }

        return nil
    }
}



struct CountdownUserDefaultsState: Codable {
    var id: String
    var state: String
    var start: Date? = nil
    var end: Date? = nil
}
