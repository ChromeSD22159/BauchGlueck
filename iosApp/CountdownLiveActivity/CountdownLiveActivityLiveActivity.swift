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
    
    var body: some WidgetConfiguration {
        return ActivityConfiguration(for: CountdownLiveActivityAttributes.self) { context in
            
            lockScreen(endDate: context.state.endDate, name: context.attributes.name)
            

        } dynamicIsland: { context in
            DynamicIsland {
                    DynamicIslandExpandedRegion(.leading) {
                        DynamicIslandCompact(endDate: context.state.endDate, name: context.attributes.name, position: .LargeLeading)
                    }
                
                    DynamicIslandExpandedRegion(.trailing) {
                        DynamicIslandCompact(endDate: context.state.endDate, name: context.attributes.name, position: .LargeTrailing)
                    }
                    
                    DynamicIslandExpandedRegion(.bottom) {
                        DynamicIslandCompact(endDate: context.state.endDate, name: context.attributes.name, position: .LargeBottom)
                    }
            } compactLeading: {
                DynamicIslandCompact(endDate: context.state.endDate, name: context.attributes.name, position: .SmallLeading)
            } compactTrailing: {
                DynamicIslandCompact(endDate: context.state.endDate, name: context.attributes.name, position: .SmallTrailing)
            } minimal: {
                DynamicIslandCompact(endDate: context.state.endDate, name: context.attributes.name, position: .Minimal)
            }
            .widgetURL(URL(string: "BauchGlueck://test"))
            .keylineTint(Color.red)
        }
    }
    
    @ViewBuilder func TimerView(date: Date) -> some View {
       HStack {
           let range = Date()...Date().addingTimeInterval((date.timeIntervalSinceNow))
           Text(
              timerInterval: range,
              pauseTime: range.lowerBound
           )
           .multilineTextAlignment(.trailing)
           .foregroundStyle(Color.textRegular)
       }
   }
    
    @ViewBuilder func lockScreen(endDate: Date, name: String) -> some View {
        let range = Date()...Date().addingTimeInterval((endDate.timeIntervalSinceNow))
        
        ZStack {
            theme.color(.backgroundVariant)
            
            VStack {
                HStack(spacing: 12) {
                    Image(.stromach)
                        .font(.subheadline)
                        .foregroundStyle(theme.color(.textRegular))
                    
                    Text("\(name) timer")
                        .font(.seat(size: .subheadline))
                }
                
                Spacer()

                Text(
                   timerInterval: range,
                   pauseTime: range.lowerBound
                )
                .font(.seat(size: .largeTitle))
                .multilineTextAlignment(.center)
                
                Spacer()
                
                Text("end \(name)")
                    .font(.seat(size: .caption))
                    .foregroundStyle(theme.color(.textRegular))
                    .padding(.vertical, 5)
                    .padding(.horizontal, 10)
                    .background {
                        RoundedRectangle(cornerRadius: theme.cornerRadius)
                            .fill(.ultraThinMaterial)
                            .strokeBorder(theme.gradient(.primary), lineWidth: 1)
                    }
                    
            }
            .padding()
            .background {
                ZStack {
                    HStack {
                        Image(.stromach)
                            .font(.system(size: 150))
                            .foregroundStyle(theme.color(.textRegular).opacity(0.05))
                        
                        Text("BauchGlück")
                            .font(.seat(size: 75))
                            .foregroundStyle(theme.color(.textRegular).opacity(0.0))
                    }
                    
                    Text("BauchGlück")
                        .font(.seat(size: 50))
                        .foregroundStyle(theme.color(.textRegular).opacity(0.05))
                }
            }

        }
    }
    
    @ViewBuilder func DynamicIslandCompact(endDate: Date, name:String, position: ActivityPosition) -> some View {
        switch position {
            case .LargeLeading: Image(.stromach).foregroundStyle(theme.color(.secondary))
            case .LargeTrailing: Text(name).font(.seat(size: .footnote))
            case .LargeBottom: ZStack {
                
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
                            
                            TimerView(date: endDate)
                                .font(.seat(size: 36))
                            
                        }
                    }
                }
                .padding(12)
            }
            case .SmallLeading: Image(systemName: "timer")
            case .SmallTrailing: TimerView(date: endDate).font(.seat(size: .body))
            case .Minimal: TimerView(date: endDate).font(.seat(size: .caption2))
        }
        
    }
    
    
}

enum ActivityPosition {
    case LargeLeading, LargeTrailing, LargeBottom, SmallLeading, SmallTrailing, Minimal
}

extension CountdownLiveActivityAttributes {
    fileprivate static var preview: CountdownLiveActivityAttributes {
        CountdownLiveActivityAttributes(name: "Essen Timer")
    }
}

extension CountdownLiveActivityAttributes.ContentState {
    fileprivate static var smiley: CountdownLiveActivityAttributes.ContentState {
        CountdownLiveActivityAttributes.ContentState(state: "running", startDate: Date(), endDate: Calendar.current.date(byAdding: .second, value: 60, to: Date())!, remainingDuration: 2200)
     }
}

#Preview("Notification", as: .dynamicIsland(.minimal), using: CountdownLiveActivityAttributes.preview) {
   CountdownLiveActivityLiveActivity()
} contentStates: {
    CountdownLiveActivityAttributes.ContentState.smiley
}
