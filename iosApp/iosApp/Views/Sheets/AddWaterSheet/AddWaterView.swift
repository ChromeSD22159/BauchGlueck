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
        ZStack {
            BackgroundImage()
            VStack {
                Header()
                
                Spacer()
                
                VStack(spacing: 50) {
                    Spacer()
                    ProgressCircle()
                    
                    Text("Getränkemenge")
                        .font(.title2)
                        .bold()
                    
                    ControlButtons()
                    
                    SaveControlButtons()
                    
                    Spacer()
                }
                
            }.padding(16)
        }
    }
    
    @ViewBuilder func Header() -> some View {
        HStack {
            Text("Add Water")
                .foregroundStyle(theme.color(.primary))
                .font(.kodchasanBold(size: .title3))
            
            Spacer()
            
            Button {
                awvm.isAddWaterSheet.toggle()
            } label: {
                HStack {
                    Image(systemName: "xmark")
                }.foregroundStyle(theme.color(.textRegular))
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
                .trim(from: 0.0, to: CGFloat(min(Double(awvm.drinkAmount) / Double(awvm.totalAmount), 1.0)))
                .stroke(style: StrokeStyle(lineWidth: 20, lineCap: .round, lineJoin: .round))
                .foregroundColor(theme.color(.primary))
                .rotationEffect(Angle(degrees: 270.0))
                .animation(.linear, value: 5)

            Text("\(Int((Double(awvm.drinkAmount) / Double(awvm.totalAmount)) * 100))%")
                .font(.seat(size: .title))
                .bold()
        }
        .frame(width: 150, height: 150)
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
    
     
    
    @ViewBuilder func SaveControlButtons() -> some View {
        HStack(spacing: 20){
            Button {
                awvm.isAddWaterSheet.toggle()
            } label: {
                HStack {
                    Spacer()
                    Text("Save")
                    Spacer()
                }
                .background(theme.gradient(.primary))
                .cornerRadius(theme.cornerRadius)
            }
            .padding(.vertical, 5)
            .foregroundColor(theme.color(.textComplimentary))
            .background(theme.gradient(.primary))
            .cornerRadius(theme.cornerRadius)
            
            Button {
                Task {
                    awvm.saveWaterToHealth()
                    
                    await awvm.wait(forSeconds: 0.75)
                    
                    awvm.isAddWaterSheet.toggle()
                }
            } label: {
                HStack {
                    Spacer()
                    Text("Save")
                        .font(.body)
                    Spacer()
                }
            }
            .padding(.vertical, 5)
            .foregroundColor(theme.color(.textComplimentary))
            .background(theme.gradient(.primary))
            .cornerRadius(theme.cornerRadius)
        }
    }
}


#Preview("AddWaterView") {
    AddWaterView(awvm: AddWaterViewModel())
        .environmentObject(Theme())
}
