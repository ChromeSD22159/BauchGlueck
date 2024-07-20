//
//  TimerState.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 19.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

enum TimerState: String, Codable {
    case running = "running"
    case paused = "paused"
    case completed = "completed"
    case notRunning = "notRunning"
}
