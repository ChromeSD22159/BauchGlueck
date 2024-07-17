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
        ZStack {
            BackgroundImage()
            VStack {
                Header()
                    .padding(.bottom, 175)
                
                List {
                    Picker("", selection: atvm.timerTypeBinding) {
                        ForEach(atvm.pickerChoose, id: \.hashValue) { choose in
                            Text(choose.rawValue).tag(choose)
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
                            "\(atvm.timerDurationBinding.wrappedValue) minute timer",
                            value: atvm.timerDurationBinding,
                            in: 5...60,
                            step: 5
                        )
                    }
                    .listRowBackground(theme.color(.backgroundVariant).opacity(0.9))
                    .listRowSeparator(.hidden)
                    
                    Section{
                        HStack(spacing: 20){
                            Button {
                                atvm.isAddTimerSheet = false
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
                                    
                                    DispatchQueue.main.asyncAfter(deadline: .now() + 6.25) {
                                        
                                        if shouldSaveTimer {
                                            guard atvm.saveTimer() else { return }
                                        }
                                        
                                        atvm.isAddTimerSheet = false
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
                                    .opacity(atvm.isSyncDoneAnimation ? 1 : 0)
                                
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: theme.color(.secondaryVariant)))
                                     .scaleEffect(2.0, anchor: .center)
                                     .opacity(atvm.isSyncAnimation ? 1 : 0)
                            
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
        .onAppear(perform: {
            if let user = authManager.user {
                atvm.initTimer(user: user)
            }
        })
    }
    
    @ViewBuilder func Header() -> some View {
        HStack {
            Text("Add Timer")
                .foregroundStyle(theme.color(.primary))
                .font(.kodchasanBold(size: .title3))
                
            
            Spacer()
            
            Button {
                atvm.isAddTimerSheet.toggle()
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

#Preview {
    AddTimerView(atvm: AddTimerViewModel())
     .environmentObject(Theme())
     .environmentObject(FirebaseAuthManager())
     .preferredColorScheme(.dark)
}
