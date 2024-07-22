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
    let firestoreTimerManager = FirestoreTimerManager.shared
    
    @Published var countdown: CountdownTimer
    @Published var countdownCopy: CountdownTimer
    
    @Published var remainingTime: Int = 0
    @Published var remainingTimeString: String = ""
    @Published var isPast: Bool = false
    
    @AppStorage("activityID") var activityID: String = ""
    
    var calcDurationPercent: Int {
       if countdown.remainingDuration <= 0 {
           print("\(countdown.name) percent: 100")
           return 100
       } else {
           let percent = (countdown.remainingDuration * 100) / countdown.duration
           print("\(countdown.name) percent: \(percent)")
           return percent
       }
   }
    
    init(countdown: CountdownTimer) {
        self.countdown = countdown
        self.countdownCopy = countdown
        self.remainingTimeString = formateRemainingDuration(duration: Double(countdown.remainingDuration))
    }

    func start() {
        countdown.timerState = TimerState.running.rawValue
        countdown.startDate = Date()
        countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.duration))
        remainingTime = countdown.duration
        startTicker()

        firestoreTimerManager.saveCountdownToUserDefaults(countdown: countdown)
        
        if let end = countdown.endDate {
            sendNotification(
                countdown: countdown,
                date: end
            )
        }
        
        Task {
            await LiveActivityStart(withTimer: countdown)
        }
        
        self.objectWillChange.send()
    }

    func stop() {
        countdown.timerState = TimerState.notRunning.rawValue
        countdown.startDate = nil
        countdown.endDate = nil
        countdown.remainingDuration = countdownCopy.remainingDuration
        remainingTime = countdownCopy.remainingDuration
        remainingTimeString = formateRemainingDuration(duration: Double(remainingTime))
        stopTicker()

        firestoreTimerManager.removeCountdownFromUserDefaults(countdown: countdown)
        
        Task {
            await LiveActivityEnd()
            removeNotification(withIdentifier: countdown.id)
        }
        
        print("stop")
        newUpdater()
        
        self.objectWillChange.send()
    }

    func pause() {
        stopTicker()
        countdown.timerState = TimerState.paused.rawValue
        if let endDate = countdown.endDate {
          countdown.remainingDuration = Int(endDate.timeIntervalSinceNow)
          remainingTime = countdown.remainingDuration
          remainingTimeString = formateRemainingDuration(duration: Double(remainingTime))
        }

        firestoreTimerManager.saveCountdownToUserDefaults(countdown: countdown)

        Task {
          await LiveActivityUpdate(timer: countdown)
          removeNotification(withIdentifier: countdown.id)
        }
          
        newUpdater()
          
        self.objectWillChange.send()
    }
    
    func resume() {
        if countdown.timerState == TimerState.paused.rawValue {
            countdown.startDate = Date()
            countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.remainingDuration))
            remainingTime = countdown.remainingDuration
        } else {
            countdown.startDate = Date()
            countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.duration))
            remainingTime = countdown.duration
        }
        countdown.timerState = TimerState.running.rawValue
        startTicker()

        firestoreTimerManager.saveCountdownToUserDefaults(countdown: countdown)
        
        Task {
            await LiveActivityUpdate(timer: countdown)
        }
        
        if let end = countdown.endDate {
            sendNotification(
                countdown: countdown,
                date: end
            )
        }
        
        newUpdater()
        
        self.objectWillChange.send()
    }

    func resumeAppStart(_ trigger: String) {
        if let savedState = firestoreTimerManager.loadCountdownFromUserDefaults(countdown: countdown) {
            self.countdown = savedState // Update the whole countdown object

            if self.countdown.timerState == TimerState.running.rawValue {
                let now = Date()

                if let end = countdown.endDate, end > now {
                    countdown.remainingDuration = Int(end.timeIntervalSince(now))
                    remainingTime = countdown.remainingDuration
                    startTicker()
                } else {
                    // Timer has expired, stop it and update state
                    countdown.timerState = TimerState.notRunning.rawValue
                    stopTicker()
                    remainingTime = countdown.duration
                    remainingTimeString = formateRemainingDuration(duration: Double(remainingTime))
                    objectWillChange.send() // Trigger UI update
                }
            } else {
                remainingTime = countdown.duration
                remainingTimeString = formateRemainingDuration(duration: Double(remainingTime))
                objectWillChange.send() // Trigger UI update
            }
        }
    }

    func updateNameDuration() {
        if let savedState = firestoreTimerManager.loadCountdownFromUserDefaults(countdown: countdown) {
            self.countdown.name = savedState.name
            self.countdown.duration = savedState.duration
        }
    }
    
    func calcRectangleWidth(geometryReader: GeometryProxy) -> CGFloat {
        let totalWidth = geometryReader.size.width
        return (CGFloat(calcDurationPercent) / 100.0) * totalWidth
    }
    
    private func formateRemainingDuration(duration: Double) -> String {
        let sec = Int(duration)
        let hours = sec / 3600
        let minutes = (sec % 3600) / 60
        let seconds = sec % 60
        
        if hours != 0 {
            return String(format: "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            return String(format: "%02d:%02d", minutes, seconds)
        }
    }

    func checkScenePhasesToRestart(_ newPhase: ScenePhase) {
        if newPhase == .active {
            self.resumeAppStart("checkScenePhasesToRestart active")
        } else if newPhase == .background {
            self.stopTicker()
        }
    }
    
    func newUpdater() {
        if let endDate = self.countdown.endDate {
            let remainingSeconds = endDate.timeIntervalSinceNow
            if remainingSeconds <= 0 {
                self.remainingTime = 0
                self.remainingTimeString = formateRemainingDuration(duration: 0)
                countdown.remainingDuration = 0
                self.isPast = true
            } else {
                self.remainingTime = Int(remainingSeconds)
                self.remainingTimeString = formateRemainingDuration(duration: remainingSeconds)
                countdown.remainingDuration = Int(remainingSeconds)
                self.isPast = false
            }
        }
    }
    
    // FirestoreTimerManager
    func delete() {
        FirestoreTimerManager.shared.deleteTimer(countdown: countdown)
    }
    
    // Timer.publish
    @Published var ticker = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    private var cancellable: AnyCancellable?
    
    private func startTicker() {
        cancellable = ticker.sink { _ in
            print("Tick")
            self.newUpdater()
        }
    }

    private func stopTicker() {
        cancellable?.cancel()
    }
    
    // LIVEACTIVITY
    private func LiveActivityStart(withTimer timer: CountdownTimer) async {
        guard timer.showAvtivity else { return }
        
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
                break
            }
        }
    }
    
    // NOTIFICATION
    func sendNotification(countdown: CountdownTimer, date: Date) {
        guard countdown.notificate else { return }
        
        let content = UNMutableNotificationContent()
        content.title = "BauchGlück Timer"
        content.body = "Der \(countdown.name) Timer ist beeendet."
        content.sound = UNNotificationSound.default

        let dateComponents = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .second], from: date)
        
        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: false)
        
        let request = UNNotificationRequest(identifier: countdown.id, content: content, trigger: trigger)
        UNUserNotificationCenter.current().add(request)
    }
    
    func removeNotification(withIdentifier identifier: String) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [identifier])
    }
}
