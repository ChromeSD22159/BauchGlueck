//
//  NotificationManager.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 16.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import UserNotifications

class NotificationManager {
    func requestPermisson() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { success, error in
            if success {
                print("UserNotification set!")
            } else if let error {
                print(error.localizedDescription)
            }
        }
    }
    
    func requesterNoticication(title: String, subTitle: String, min: Int = 5) {
        let content = UNMutableNotificationContent()
        content.title = "Feed the cat"
        content.subtitle = "It looks hungry"
        content.sound = UNNotificationSound.default

        // show this notification five seconds from now
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: TimeInterval(min * 60), repeats: false)

        // choose a random identifier
        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger)

        // add our notification request
        UNUserNotificationCenter.current().add(request)
    }
}
