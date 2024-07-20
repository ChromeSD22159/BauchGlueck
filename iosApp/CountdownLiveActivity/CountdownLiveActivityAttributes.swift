//
//  CountdownLiveActivityAttributes.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 20.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import ActivityKit

struct CountdownLiveActivityAttributes: ActivityAttributes {
    public struct ContentState: Codable, Hashable {
        var state: String
        var startDate: Date
        var endDate: Date
        var remainingDuration: Int
    }

    // Fixed non-changing properties about your activity go here!
    var name: String
}
