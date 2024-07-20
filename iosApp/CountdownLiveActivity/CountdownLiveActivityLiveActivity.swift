//
//  CountdownLiveActivityLiveActivity.swift
//  CountdownLiveActivity
//
//  Created by Frederik Kohler on 20.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import ActivityKit
import WidgetKit
import SwiftUI



struct CountdownLiveActivityLiveActivity: Widget {
    @StateObject var theme = Theme()
    
    @State var isDone = false
    
    var body: some WidgetConfiguration {
        return ActivityConfiguration(for: CountdownLiveActivityAttributes.self) { context in
            ZStack {
                theme.color(.backgroundVariant)
                
                VStack {
                    Spacer()
                    HStack {
                        VStack(alignment: .leading) {
                            Spacer()
                            
                            HStack(alignment: .bottom, spacing: 12) {
                                Image(systemName: "timer")
                                    .font(.system(size: 48))
                                    .foregroundStyle(theme.color(.textRegular))
                            }
                        }
                        Spacer()
                        VStack(alignment: .trailing) {
                            Spacer()
                            Text(remainingTimeInterval(endDate: context.state.endDate, name: context.attributes.name), style: .timer)
                                .font(.kodchasanBold(size: 52))
                                .foregroundStyle(theme.color(.textRegular))
                                .monospacedDigit()
                            Text(context.attributes.name)
                                .font(.kodchasanBold(size: .title))
                                .foregroundStyle(theme.color(.textRegular))
                        }
                    }
                }
                .padding(16)
            }
            .environmentObject(theme)

        } dynamicIsland: { context in
            DynamicIsland {
                    
                    DynamicIslandExpandedRegion(.leading) {
                        Image(.stromach)
                            .foregroundStyle(theme.color(.secondary))
                    }
                
                    DynamicIslandExpandedRegion(.trailing) {
                        Text(context.attributes.name)
                    }
                    
                    DynamicIslandExpandedRegion(.bottom) {
                        ZStack {
                            
                            VStack {
                                Spacer()
                                HStack {
                                    VStack(alignment: .leading) {
                                        Spacer()
                                        
                                        HStack(alignment: .bottom, spacing: 12) {
                                            Image(systemName: "timer")
                                                .font(.system(size: 36))
                                                .foregroundStyle(theme.color(.textRegular))
                                        }
                                    }
                                    Spacer()
                                    VStack(alignment: .trailing) {
                                        Spacer()
                                        Text(remainingTimeInterval(endDate: context.state.endDate, name: context.attributes.name), style: .timer)
                                            .font(.kodchasanBold(size: 36))
                                            .foregroundStyle(theme.color(.textRegular))
                                            .monospacedDigit()
                                    }
                                }
                            }
                            .padding(12)
                        }
   
                    }
            } compactLeading: {
                Image(systemName: "timer")
            } compactTrailing: {
                Text(remainingTimeInterval(endDate: context.state.endDate, name: context.attributes.name), style: .timer)
            } minimal: {
                Text(remainingTimeInterval(endDate: context.state.endDate, name: context.attributes.name), style: .timer)
            }
            .widgetURL(URL(string: "BachGlueck://test"))
            .keylineTint(Color.red)
        }
    }

    func timerString(_ remainingDuration: Int) -> String {
        let min = remainingDuration / 60
        let sec = remainingDuration % 60
        return String(format: "%02d:%02d", min, sec)
    }
    
    func remainingTimeInterval(endDate: Date, name: String) -> Date {
        let remainingSeconds = endDate.timeIntervalSinceNow
        
        if remainingSeconds <= 0 {
            return Date() // Return the current time to show "00:00"
        } else {
            return Date().addingTimeInterval(remainingSeconds)
        }
    }
    
    func sendNotification(title: String, body: String, delay: TimeInterval = 5) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = UNNotificationSound.default

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: delay, repeats: false)
        let request = UNNotificationRequest(identifier: UUID().uuidString, content: content, trigger: trigger) // Use the trigger here
        
        UNUserNotificationCenter.current().add(request)
    }
}

extension CountdownLiveActivityAttributes {
    fileprivate static var preview: CountdownLiveActivityAttributes {
        CountdownLiveActivityAttributes(name: "Essen Timer")
    }
}

extension CountdownLiveActivityAttributes.ContentState {
    fileprivate static var smiley: CountdownLiveActivityAttributes.ContentState {
        CountdownLiveActivityAttributes.ContentState(state: "notRunning", startDate: Date(), endDate: Date(), remainingDuration: 2200)
     }
     
     fileprivate static var starEyes: CountdownLiveActivityAttributes.ContentState {
         CountdownLiveActivityAttributes.ContentState(state: "notRunning", startDate: Date(), endDate: Date(), remainingDuration: 2200)
     }
}

#Preview("Notification", as: .content, using: CountdownLiveActivityAttributes.preview) {
   CountdownLiveActivityLiveActivity()
} contentStates: {
    CountdownLiveActivityAttributes.ContentState.smiley
    CountdownLiveActivityAttributes.ContentState.starEyes
}
