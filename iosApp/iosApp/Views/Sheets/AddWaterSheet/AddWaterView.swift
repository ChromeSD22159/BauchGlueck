//
//  AddWaterView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct AddWaterView: View {
    @EnvironmentObject var theme: Theme
    @ObservedObject var awvm: AddWaterViewModel
    
    var body: some View {
        SheetView(sheetName: "Add Water", closeBinding: $awvm.isAddWaterSheet) {
            VStack(spacing: 50) {
                Spacer()
                ProgressCircle()
                
                Text("Amount of drink")
                    .font(.title2)
                    .bold()
                
                ControlButtons()
                
                Spacer()
            }
        } closeFunc: {
            // closeClock
        } addFunc: {
            // ActionBlock
            Task {
                awvm.saveWaterToHealth()
                
                await awvm.wait(forSeconds: 0.75)
            }
        }
    }
    
    @ViewBuilder func ProgressCircle() -> some View {
        ZStack {
            ProgressBackgroundCircle()
            ProgressCircleTrim()
            ProgressText()
        }
        .frame(width: 150, height: 150)
    }

    private func ProgressBackgroundCircle() -> some View {
        Circle()
            .stroke(lineWidth: 20)
            .opacity(0.3)
            .foregroundColor(theme.color(.primary))
    }

    private func ProgressCircleTrim() -> some View {
        Circle()
            .trim(from: 0.0, to: awvm.trimPercent)
            .stroke(style: StrokeStyle(lineWidth: 20, lineCap: .round, lineJoin: .round))
            .foregroundColor(theme.color(.primary))
            .rotationEffect(Angle(degrees: 270.0))
            .animation(.linear, value: awvm.trimPercent) // Animate trimPercent changes
    }

    private func ProgressText() -> some View {
        Text("\(awvm.intakePercentPerDay)%")
            .font(.seat(size: .title))
            .bold()
    }
    
    @ViewBuilder func ControlButtons() -> some View {
        HStack {
            Button(action: {
                awvm.decrease()
                
            }) {
                Image(systemName: "minus.circle.fill")
                    .resizable()
                    .frame(width: 50, height: 50)
                    .foregroundColor(theme.color(.primary))
            }
            
            Text("\(awvm.drinkAmount) ml")
                .font(.seat(size: .title))
                .frame(width: 150)
                .padding(.horizontal, 20)
            
            Button(action: {
                awvm.increase()
            }) {
                Image(systemName: "plus.circle.fill")
                    .resizable()
                    .frame(width: 50, height: 50)
                    .foregroundColor(theme.color(.primary))
            }
        }
    }
}


#Preview("AddWaterView") {
    AddWaterView(awvm: AddWaterViewModel())
        .environmentObject(Theme())
}
