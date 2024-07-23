//
//  AddWeightView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct AddWeightView: View {
    @EnvironmentObject var theme: Theme
    @ObservedObject var awvm: AddWeightViewModel
    
    var body: some View {
        SheetView(sheetName: "Add Weight", closeBinding: $awvm.isAddWeightSheet) {
            VStack(spacing: 50) {
                Spacer()
                ProgressCircle()
                
                Text("Gewicht")
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
                awvm.saveWeightToHealth()
                
                await awvm.wait(forSeconds: 0.75)
            }
        }
    }
    
    @ViewBuilder func ProgressCircle() -> some View {
        ZStack {
            Circle()
                .stroke(lineWidth: 20)
                .opacity(0.3)
                .foregroundColor(theme.color(.primary))

            Circle()
                .trim(from: 0.0, to: CGFloat(min(Double(awvm.currentWeight) / Double(awvm.targetWeight), 1.0)))
                .stroke(style: StrokeStyle(lineWidth: 20, lineCap: .round, lineJoin: .round))
                .foregroundColor(theme.color(.primary))
                .rotationEffect(Angle(degrees: 270.0))
                .animation(.linear, value: 5)

            Text("\(Int((Double(awvm.currentWeight))))kg") //  / Double(awvm.targetWeight)) * 100
                .font(.seat(size: .title))
                .bold()
        }
        .frame(width: 150, height: 150)
    }
    
    @ViewBuilder func ControlButtons() -> some View {
        VStack(spacing: 50) {
            HStack(spacing: 25) {
                Button(action: {
                    awvm.decrease()
                }) {
                    Image(systemName: "minus.circle.fill")
                        .resizable()
                        .frame(width: 50, height: 50)
                        .foregroundColor(theme.color(.primary))
                }
                
                Text("\(awvm.currentWeight, specifier: "%.1f") kg")
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
            
          
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 10) {
                    ForEach(awvm.weights, id: \.self) { kilo in
                        Button(action: {
                            awvm.currentWeight = kilo
                        }) {
                            Text("\(kilo, specifier: "%.0f")kg")
                                .font(.seat(size: .body))
                                .foregroundColor(theme.color(.primary))
                        }
                    }
                }
            }
            
            
        }
    }
}

struct AddWeightView_Previews: PreviewProvider {
    static var previews: some View {
        // Erstelle ein Dummy-Theme für die Vorschau
        let dummyTheme = Theme()
        
        // Erstelle ein Dummy-ViewModel mit Beispielwerten
        let dummyViewModel = AddWeightViewModel()
        
        // Erstelle die Vorschauansicht
        AddWeightView(awvm: dummyViewModel)
            .environmentObject(dummyTheme)
    }
}
