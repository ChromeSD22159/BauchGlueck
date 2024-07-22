//
//  WaterIntakeCardView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import Charts
import SwiftUI

struct WaterIntakeCardView: View {
    @EnvironmentObject var theme: Theme
    
    @ObservedObject var healthManager = HealthManager.shared
    
    var color: Color {
        isEnoughData ? theme.color(.textComplimentary).opacity(1) : theme.color(.textComplimentary).opacity(0.5)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Header()

            Chart {
                let data = healthManager.waterList
                ForEach(data, id: \.date) { intake in
                    BarMark(
                        x: .value("Datum", intake.date, unit: .day),
                        y: .value("Aufnahme", intake.intake)
                    )
                    .foregroundStyle(color)
                }
            }
            .chartXAxis {
                AxisMarks(values: .stride(by: .day)) { value in
                    AxisGridLine()
                    AxisTick()
                    if let dateValue = value.as(Date.self) {
                        AxisValueLabel {
                            HStack(alignment: .center) {
                                Text(formatDateToInt(dateValue))
                                    .font(.seat(size: 10))
                                    .foregroundStyle(color)
                            }
                            .frame(maxWidth: .infinity, alignment: .center)
                        }
                    }
                }
            }
            .chartYAxis {
                AxisMarks(values: .automatic(desiredCount: 5)) { value in
                    AxisGridLine()
                    AxisTick()
                    AxisValueLabel {
                        if let literValue = value.as(Double.self) {
                            HStack(alignment: .center) {
                                Spacer()
                                Text("\(literValue, specifier: "%.1f") L")
                                    .font(.seat(size: 10))
                                    .foregroundStyle(color)
                            }
                            .frame(maxWidth: .infinity, alignment: .trailing)
                        }
                    }
                }
            }
            .frame(height: 150)
        }
        .padding(8)
        .background(
            RoundedRectangle(cornerRadius: 10)
                .fill(theme.color(.secondary))
        )
    }
    
    @ViewBuilder func Header() -> some View {
        HStack {
            Text("Water absorption")
                .font(.seat(size: .headline))
                .foregroundStyle(theme.color(.textComplimentary))
            
            Spacer()
            
            Image(systemName: calculateTrendImage)
            
            Text(calculateTrend)
                .font(.caption)
        }
    }
    
    func formatDateToInt(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "d"
        return formatter.string(from: date)
    }
    
    var calculateTrendImage: String {
        let list = healthManager.waterList
        
        guard list.count >= 2 else { return "Not enough data" }

        let lastValue = list[list.count - 1].intake
        let secondToLastValue = list[list.count - 2].intake

        if lastValue > secondToLastValue {
            return "arrow.up.forward.circle.fill"
        } else if lastValue < secondToLastValue {
            return "arrow.down.forward.circle.fill"
        } else {
            return "equal.circle.fill"
        }
    }
    
    var calculateTrend: LocalizedStringKey {
        let list = healthManager.waterList
        
        guard list.count >= 2 else { return "Not enough data" }

        let lastValue = list[list.count - 1].intake
        let secondToLastValue = list[list.count - 2].intake

        if lastValue > secondToLastValue {
            return "Ascending"
        } else if lastValue < secondToLastValue {
            return "Descending"
        } else {
            return "Consistent"
        }
    }
    
    var isEnoughData: Bool {
        healthManager.waterList.count >= 2
    }
}

#Preview("WaterIntakeCardView") {
    WaterIntakeCardView()
        .environmentObject(Theme())
        .preferredColorScheme(.light)
}

