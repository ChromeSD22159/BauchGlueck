//
//  AddTimerView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct AddTimerView: View {
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var authManager: FirebaseAuthManager
    @ObservedObject var atvm: AddTimerViewModel
    @State private var shouldSaveTimer = false
    
    var body: some View {
        SheetView(sheetName: "Add Timer", closeBinding: $atvm.isAddTimerSheet) {
            List {
                Section {
                    Text("Anzahl Timer: \(FirestoreTimerManager.shared.timerList.count)")
                        .foregroundStyle(theme.color(.primary))
                        .font(.kodchasanBold(size: .title))
                }
                .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                .listRowSeparator(.hidden)
                
                Picker("", selection: atvm.timerTypeBinding) {
                    ForEach(atvm.pickerChoose, id: \.rawValue) { choose in
                        Text(choose.rawValue).tag(choose.rawValue)
                    }
                }
                .pickerStyle(.segmented)
                .listRowInsets(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 0))
                .listRowBackground(Color.clear)
                .listRowSeparator(.hidden)
                
                Section {
                    TextField(text: atvm.timerNameBinding, label: { Text("Timer Name:") })
                        .textFieldClearButton(text: atvm.timerNameBinding)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
                .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                .listRowSeparator(.hidden)
                
                Section {
                    Stepper(
                        "\(atvm.timerDurationBinding.wrappedValue / 60) minute timer",
                        value: atvm.timerDurationBinding,
                        in: (5 * 60)...(60 * 60),
                        step: (5 * 60)
                    )
                }
                .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                .listRowSeparator(.hidden)
            }
            .scrollContentBackground(.hidden)
            .background(Color.clear)
        } closeFunc: {
            // closeClock
        } addFunc: {
            // ActionBlock
            Task {
                shouldSaveTimer = true
                
                withAnimation(.linear(duration: 0.250)) {
                    atvm.isSyncAnimation = true
                }
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                    withAnimation(.linear(duration: 0.250)) {
                        atvm.isSyncAnimation = false
                    }
                }
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 2.25) {
                    withAnimation(.linear(duration: 0.250)) {
                        atvm.isSyncDoneAnimation = true
                    }
                }
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 3.75) {
                    withAnimation(.linear(duration: 0.250)) {
                        atvm.isSyncDoneAnimation = false
                    }
                }
                
                if shouldSaveTimer {
                    atvm.saveTimer(complete: { timer, bool in
                        if let timer = timer {
                            FirestoreTimerManager.shared.timerList.append(timer)
                        }
                    })
                    
                }
                
                DispatchQueue.main.asyncAfter(deadline: .now() + 6.25) {
                    atvm.isAddTimerSheet = false
                }
            }
        }
        .onAppear {
            if let user = authManager.user {
                atvm.initTimer(user: user)
            }
        }
    }
}

#Preview {
    AddTimerView(atvm: AddTimerViewModel())
     .environmentObject(Theme())
     .environmentObject(FirebaseAuthManager.shared)
     .preferredColorScheme(.dark)
}
