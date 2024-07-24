//
//  WeightCardView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Charts

struct WeightCardView: View {
    @EnvironmentObject var theme: Theme
    
    @ObservedObject var healthManager = HealthManager.shared
    
    var color: Color {
        isEnoughData ? theme.color(.textComplimentary).opacity(1) : theme.color(.textComplimentary).opacity(0.5)
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Header()

            Chart {
                let data = healthManager.weightList
                ForEach(data, id: \.date) { record in
                    BarMark(
                        x: .value("Datum", record.date, unit: .day),
                        y: .value("Weight", record.weight)
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
                        if let weightValue = value.as(Double.self) {
                            HStack(alignment: .center) {
                                Spacer()
                                Text("\(weightValue, specifier: "%.1f") kg")
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
            Text("Weight Records")
                .font(.seat(size: .headline))
                .foregroundStyle(theme.color(.textComplimentary))
            
            Spacer()
            
            Image(systemName: calculateTrendImage)
            
            Text(calculateTrend)
                .font(.caption)
        }
        .foregroundStyle(theme.color(.textComplimentary))
    }
    
    func formatDateToInt(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "d"
        return formatter.string(from: date)
    }

    var calculateTrendImage: String {
        let list = healthManager.weightList
        
        guard list.count >= 2 else { return "Not enough data" }

        let lastValue = list[list.count - 1].weight
        let secondToLastValue = list[list.count - 2].weight

        if lastValue > secondToLastValue {
            return "arrow.up.forward.circle.fill"
        } else if lastValue < secondToLastValue {
            return "arrow.down.forward.circle.fill"
        } else {
            return "equal.circle.fill"
        }
    }

    var calculateTrend: LocalizedStringKey {
        guard healthManager.weightList.count >= 2 else { return "Not enough data" }
        
        let list = healthManager.weightList
        
        let lastValue = list[list.count - 1].weight
        let secondToLastValue = list[list.count - 2].weight

        if lastValue > secondToLastValue {
            return "Ascending"
        } else if lastValue < secondToLastValue {
            return "Descending"
        } else {
            return "Consistent"
        }
    }

    var isEnoughData: Bool {
        healthManager.weightList.count >= 2
    }
}
