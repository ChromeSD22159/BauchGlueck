//
//  EditTimerView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 19.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct EditTimerView: View {
    @EnvironmentObject var theme: Theme
    @ObservedObject var vm: EditTimerViewModel = EditTimerViewModel.shared
    @EnvironmentObject var firestoreTimerManager: FirestoreTimerManager
    @State private var shouldSaveTimer = false
    
    var body: some View {
        ZStack {
            BackgroundImage()
            
            VStack {
                Header()
                    .padding(.bottom, 175)
                
                List {
                    Section {
                        Text("Anzahl Timer: \(firestoreTimerManager.timerList.count)")
                            .foregroundStyle(theme.color(.primary))
                            .font(.kodchasanBold(size: .title))
                    }
                    .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                    .listRowSeparator(.hidden)
                    
                    Picker("", selection: vm.timerTypeBinding) {
                        ForEach(vm.pickerChoose, id: \.rawValue) { choose in
                            Text(choose.rawValue).tag(choose.rawValue)
                        }
                    }
                    .pickerStyle(.segmented)
                    .listRowInsets(EdgeInsets(top: 0, leading: 0, bottom: 0, trailing: 0))
                    .listRowBackground(Color.clear)
                    .listRowSeparator(.hidden)
                    
                    Section {
                        TextField(text: vm.timerNameBinding, label: { Text("Timer Name:") })
                            .textFieldClearButton(text: vm.timerNameBinding)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                    .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                    .listRowSeparator(.hidden)
                    
                    Section {
                        Stepper(
                            "\(vm.timerDurationBinding.wrappedValue / 60) minute timer",
                            value: vm.timerDurationBinding,
                            in: (5 * 60)...(60 * 60),
                            step: (5 * 60)
                        )
                    }
                    .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                    .listRowSeparator(.hidden)
                    
                    Section{
                        HStack(spacing: 20){
                            Button {
                                vm.resetTimer()
                                vm.isEditTimerSheet = false
                            } label: {
                                HStack {
                                    Spacer()
                                    Text("Cancel")
                                    Spacer()
                                }
                            }
                            
                            Button {
                                Task {
                                    shouldSaveTimer = true
                                    
                                    withAnimation(.linear(duration: 0.250)) {
                                        vm.isSyncAnimation = true
                                    }
                                    
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                                        withAnimation(.linear(duration: 0.250)) {
                                            vm.isSyncAnimation = false
                                        }
                                    }
                                    
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 2.25) {
                                        withAnimation(.linear(duration: 0.250)) {
                                            vm.isSyncDoneAnimation = true
                                        }
                                    }
                                    
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 3.75) {
                                        withAnimation(.linear(duration: 0.250)) {
                                            vm.isSyncDoneAnimation = false
                                        }
                                    }
                                    
                                    if shouldSaveTimer {
                                        vm.saveEditTimer()
                                    }
                                    
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 6.25) {
                                        vm.isEditTimerSheet = false
                                    }
                                }
                            } label: {
                                HStack {
                                    Spacer()
                                    Text("Save")
                                    Spacer()
                                }
                            }
                           
                        }
                        .foregroundStyle(theme.color(.textRegular))
                    }
                    .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                    .listRowSeparator(.hidden)
                    
                    Section {
                        HStack {
                            Spacer()
                            
                            ZStack {
                                Text("Saved!")
                                    .foregroundStyle(theme.color(.secondaryVariant))
                                    .opacity(vm.isSyncDoneAnimation ? 1 : 0)
                                
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: theme.color(.secondaryVariant)))
                                     .scaleEffect(2.0, anchor: .center)
                                     .opacity(vm.isSyncAnimation ? 1 : 0)
                            
                            }
                           
                            Spacer()
                        }
                    }
                    .listRowBackground(Color.clear)
                    .listRowSeparator(.hidden)
                    
                }
                .scrollContentBackground(.hidden)
                .background(Color.clear)
            }
        }
    }
    
    @ViewBuilder func Header() -> some View {
        HStack {
            Text("Edit Timer")
                .foregroundStyle(theme.color(.primary))
                .font(.kodchasanBold(size: .title))
                
            
            Spacer()
            
            Button {
                vm.closeEditSheet()
            } label: {
                HStack {
                    Image(systemName: "xmark")
                        .font(.title)
                }
            }
        }
        .foregroundStyle(theme.color(.textRegular))
        .padding(32)
    }
}
