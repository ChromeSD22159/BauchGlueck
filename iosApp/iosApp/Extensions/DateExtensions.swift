//
//  DateExtensions.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

extension Date {
    var getWeekday: String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "E"
        return dateFormatter.string(from: self)
    }
    
    func startOfWeek(using calendar: Calendar = .current) -> Date? {
        let components = calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: self)
        return calendar.date(from: components)
    }
    
    func datesOfWeek(using calendar: Calendar = .current) -> [Date]? {
        guard let startOfWeek = self.startOfWeek(using: calendar) else { return nil }
        var dates: [Date] = []

        for i in 0..<7 {
            if let date = calendar.date(byAdding: .day, value: i, to: startOfWeek) {
                dates.append(date)
            }
        }

        return dates
    }
}
