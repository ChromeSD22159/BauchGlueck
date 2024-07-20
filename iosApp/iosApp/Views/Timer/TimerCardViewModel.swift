//
//  TimerCardViewModel.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 20.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Combine
import SwiftUI
import ActivityKit

class TimerCardViewModel: ObservableObject {
    @Published var countdown: CountdownTimer
    @Published var countdownCopy: CountdownTimer
    @State var ticker = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    @AppStorage("activityID") var activityID: String = ""
    
    var calcDurationPercent: Int {
        if countdown.remainingDuration <= 0 {
            return 100
        } else {
            let percent = (countdown.remainingDuration * 100) / countdown.duration
            return percent
        }
    }
    
    init(countdown: CountdownTimer) {
        self.countdown = countdown
        self.countdownCopy = countdown
    }

    func start() {
        countdown.timerState = TimerState.running.rawValue
        countdown.startDate = Date()
        countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.duration))
        startTicker()
     
        objectWillChange.send()
        
        saveCountdownToUserDefaults()
        
        if let end = countdown.endDate {
            sendNotification(
                timerId: countdown.id,
                title: "BauchGlück Timer",
                body: "Der \(countdown.name) Timer ist beeendet.",
                date: end
            )
        }
        
        Task {
            await LiveActivityStart(withTimer: countdown)
        }
    }

    func stop(sendNote: Bool = true) {
        countdown.timerState = TimerState.notRunning.rawValue
        countdown.startDate = nil
        countdown.endDate = nil
        countdown.remainingDuration = countdownCopy.remainingDuration
        stopTicker()
     
        objectWillChange.send()
        
        removeCountdownFromUserDefaults()
        
        Task {
            await LiveActivityEnd()
            removeNotification(withIdentifier: countdown.id)
        }
    }

    func pause() {
        stopTicker()
        countdown.timerState = TimerState.paused.rawValue
      
        objectWillChange.send()
        saveCountdownToUserDefaults()
        
        Task {
            await LiveActivityUpdate(timer: countdown)
            removeNotification(withIdentifier: countdown.id)
        }
    }
    
    func resume() {
        if countdown.timerState == TimerState.paused.rawValue {
            countdown.startDate = Date()
            countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.remainingDuration))
        } else {
            countdown.startDate = Date()
            countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.duration))
        }
        countdown.timerState = TimerState.running.rawValue
        startTicker()
        objectWillChange.send()
        saveCountdownToUserDefaults()
        
        Task {
            await LiveActivityUpdate(timer: countdown)
        }
        
        if let end = countdown.endDate {
            sendNotification(
                timerId: countdown.id,
                title: "BauchGlück Timer",
                body: "Der \(countdown.name) Timer ist beeendet.",
                date: end
            )
        }
    }
    
    func resumeAppStart() {
        if let savedState = loadCountdownFromUserDefaults() {
            self.countdown = savedState
            
            if self.countdown.timerState == TimerState.running.rawValue {
                let now = Date()
                
                if let end = countdown.endDate {
                    let differenceSeconds = Int(end.timeIntervalSince(now))
                    
                    if differenceSeconds > 0 {
                        countdown.remainingDuration = differenceSeconds
                        countdown.startDate = now
                        startTicker()
                    } else {
                        stop()
                    }
                }
            }
        }
    }

    func updateTimer(sendNote: Bool) {
        guard countdown.timerState == TimerState.running.rawValue else { return }
        
        if let end = countdown.endDate {

            let differenceSecondsInteger = Int(end.timeIntervalSince(Date()))
            
            if differenceSecondsInteger > 0 {
                countdown.remainingDuration = differenceSecondsInteger
            } else {
                stop(sendNote: sendNote)
            }
            
        }
        
        objectWillChange.send()
    }
    
    func calcRectangleWidth(geometryReader: GeometryProxy) -> CGFloat {
        let totalWidth = geometryReader.size.width
        return (CGFloat(calcDurationPercent) / 100.0) * totalWidth
    }

    var formatTime: String {
        let sec = countdown.remainingDuration
        let minutes = (sec % 3600) / 60
        let seconds = sec % 60
        return String(format: "%02d:%02d", minutes, seconds)
    }
    
    func saveCountdownToUserDefaults() {
        let defaults = UserDefaults.standard
        if let encoded = try? JSONEncoder().encode(countdown) {
            defaults.set(encoded, forKey: countdown.id)
        }
    }
    
    private func removeCountdownFromUserDefaults() {
        let defaults = UserDefaults.standard
        defaults.removeObject(forKey: countdown.id)
    }
        
    func loadCountdownFromUserDefaults() -> CountdownTimer? {
        let defaults = UserDefaults.standard
        if let savedCountdown = defaults.object(forKey: countdown.id) as? Data {
            if let loadedCountdown = try? JSONDecoder().decode(CountdownTimer.self, from: savedCountdown) {
                return loadedCountdown
            }
        }
        return nil
    }
    
    private func startTicker() {
        ticker = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    }

    func stopTicker() {
        ticker = Timer.publish(every: 0, on: .main, in: .common).autoconnect()
    }
    
    func checkScenePhasesToRestart(_ newPhase: ScenePhase) {
        if newPhase == .active {
            self.resumeAppStart()
        } else if newPhase == .background {
            self.stopTicker()
        }
    }
    
    func delete() {
        FirestoreTimerManager.shared.deleteTimer(countdown: countdown)
    }
    
    private func LiveActivityStart(withTimer timer: CountdownTimer) async {
        if ActivityAuthorizationInfo().areActivitiesEnabled {
            guard let startDate = timer.startDate, let endDate = timer.endDate else {
                return
            }
            
            let contentState = CountdownLiveActivityAttributes.ContentState(
                state: timer.timerState,
                startDate: startDate,
                endDate: endDate,
                remainingDuration: timer.remainingDuration
            )

            let activityAttributes = CountdownLiveActivityAttributes(name: timer.name)
            
            let activityContent = ActivityContent<CountdownLiveActivityAttributes.ContentState>(state: contentState, staleDate: nil)

            do {
                let activity = try Activity<CountdownLiveActivityAttributes>.request(
                    attributes: activityAttributes,
                    content: activityContent,
                    pushType: nil
                )
                activityID = activity.id
                print("Live Activity started with ID: \(activity.id)")
            } catch (let error) {
                print("Error starting Live Activity: \(error.localizedDescription)")
            }
        } else {
            // Handle the case where Live Activities are not enabled on the device.
            // You might want to prompt the user to enable them in Settings.
        }
    }
    
    private func LiveActivityEnd() async {
        for activity in Activity<CountdownLiveActivityAttributes>.activities {
            if activity.id == activityID {
                await activity.end(activity.content, dismissalPolicy: .immediate)
                print("Live Activity with ID \(activityID) ended.")
                activityID = ""
                
                break // Exit the loop once the activity is found
            }
        }
    }
    
    private func LiveActivityUpdate(timer: CountdownTimer) async {
        guard let startDate = timer.startDate, let endDate = timer.endDate else {
            return
        }
        
        let updatedContentState = CountdownLiveActivityAttributes.ContentState(
            state: timer.timerState,
            startDate: startDate,
            endDate: endDate,
            remainingDuration: timer.remainingDuration
        )

        let updatedContent = ActivityContent<CountdownLiveActivityAttributes.ContentState>(state: updatedContentState, staleDate: nil) // Update staleDate if needed

        for activity in Activity<CountdownLiveActivityAttributes>.activities {
            if activity.id == activityID {
                await activity.update(updatedContent)
                print("Live Activity with ID \(activityID) updated.")
                
                print("States:\(timer)")
                break // Exit the loop after updating
            }
        }
    }
    
    func sendNotification(timerId: String, title: String, body: String, date: Date) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = UNNotificationSound.default

        let dateComponents = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .second], from: date)
        
        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: false)
        
        let request = UNNotificationRequest(identifier: timerId, content: content, trigger: trigger)
        UNUserNotificationCenter.current().add(request)
        print("Notification registriert um \(Date())")
        print("\(trigger.dateComponents)")
    }
    
    func removeNotification(withIdentifier identifier: String) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [identifier])
    }
}
