//
//  FirestoreManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 18.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import Firebase

// MARK: - Firestore Timer Manager (Singleton)
class FirestoreTimerManager: ObservableObject {
    static let shared = FirestoreTimerManager() // Singleton instance

    private let db = Firestore.firestore()
    
    @Published var timerList: [CountDownTimer] = []

    func initialize(userId: String, loadLokal: Bool) {
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
                self?.timerList = documents.compactMap { try? $0.data(as: CountDownTimer.self) }
                print("initialized \(self?.timerList.count ?? -1)")
            }
    }
    
    func synronize(userId: String) {
        guard let userId = Auth.auth().currentUser?.uid else { return } // Get current user

        db.collection("timers")
            .whereField("userId", isEqualTo: userId)
            .addSnapshotListener { [weak self] querySnapshot, error in
                guard let documents = querySnapshot?.documents else {
                    print("Error fetching timers: \(error?.localizedDescription ?? "Unknown error")")
                    return
                }
                self?.timerList = documents.compactMap { try? $0.data(as: CountDownTimer.self) }
                
                print("synronized \(self?.timerList.count ?? -1)")
            }
        objectWillChange.send()
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
                self?.timerList = documents.compactMap { try? $0.data(as: CountDownTimer.self) }
            }
    }

    func saveTimer(countdown: CountDownTimer, complete: @escaping (CountDownTimer?, Bool) -> Void) {
        do {
            guard let userId = Auth.auth().currentUser?.uid else { return }
            var updatedCountdown = countdown
            updatedCountdown.userId = userId // Ensure the timer has the correct user ID
            try Firestore.firestore().collection("timers").document(countdown.id).setData(from: updatedCountdown)
            complete(updatedCountdown, true)
        } catch {
            print("Error saving timer: \(error.localizedDescription)")
            complete(nil, false)
        }
    }

    func editTimer(countdown: CountDownTimer) {
        guard let userId = Auth.auth().currentUser?.uid else {
            print("No authenticated user")
            return
        }
        
        var updatedCountdown = countdown
        updatedCountdown.userId = userId
        
        let timerRef = db.collection("timers").document(countdown.id)
        
        do {
            try timerRef.setData(from: updatedCountdown, merge: true) { [weak self] error in
            if let error = error {
                print("Error updating timer: \(error.localizedDescription)")
            } else {
                print("Timer updated successfully!")
              
                if let index = self?.timerList.firstIndex(where: { $0.id == countdown.id }) {
                    self?.timerList[index] = updatedCountdown
                }
            }
        }
        } catch {
            print("Error updating timer: \(error.localizedDescription)")
        }
    }
    
    func deleteTimer(countdown: CountDownTimer) {
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
}
