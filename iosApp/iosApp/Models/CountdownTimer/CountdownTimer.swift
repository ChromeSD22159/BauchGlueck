//
//  CountDownTimer.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 19.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

struct CountdownTimer: Identifiable, Codable {
    var id: String
    var userId: String
    var name: String
    var duration: Int
    var startDate: Date?
    var endDate: Date?
    var timerState: String
    var timerType: String
    var remainingDuration: Int
    var notificate: Bool = true
    var showAvtivity: Bool = true
}
