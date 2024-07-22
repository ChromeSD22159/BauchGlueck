//
//  Timer.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 18.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct TimerCard: View {
    @EnvironmentObject var theme: Theme
    @Environment(\.scenePhase) var scenePhase
    @StateObject var viewModel: TimerCardViewModel
    @State var countdown: CountdownTimer
    
    @StateObject var editViewModel = EditTimerViewModel.shared
    
    init(countdown: CountdownTimer) {
        self._countdown = State(initialValue: countdown)
        self._viewModel = StateObject(wrappedValue: TimerCardViewModel(countdown: countdown))
    }
    
    var body: some View {
        ZStack {
            theme.color(.backgroundVariant)
            // BACKGROUND
            HStack {
                GeometryReader { geo in
                    Rectangle()
                        .fill(theme.color(.secondary))
                        .frame(width: viewModel.calcRectangleWidth(geometryReader: geo), height: .infinity)
                    Spacer()
                }
            }
            // Content
            VStack {
                
                HStack {
                    Text(viewModel.countdown.name)
                    Spacer()
                    ContextMenu()
                }
                
                Spacer()
                HStack {
                    VStack(alignment: .leading) {
                        Spacer()
                        
                        if viewModel.countdown.timerState == TimerState.notRunning.rawValue{
                            ControllNotRunning()
                        }
                        if viewModel.countdown.timerState == TimerState.paused.rawValue {
                            ControllResumeRunning()
                        }
                        if viewModel.countdown.timerState == TimerState.running.rawValue {
                            ControllRunning()
                        }
                    }
                    Spacer()
                    VStack(alignment: .trailing) {
                        Spacer()
                        
                        Text(viewModel.remainingTimeString)
                            .font(.kodchasanBold(size: 52))
                            .foregroundStyle(theme.color(.textComplimentary))
                            .monospacedDigit()
                            .onReceive(viewModel.ticker, perform: {_ in
                                
                            })
                    }
                }
            }
            .padding(16)
        }
        .frame(maxHeight: 200)
        .cornerRadius(16)
        .onAppear(perform: {
            viewModel.resumeAppStart("onAppear")
        })
        .onChange(of: scenePhase) { newPhase in
            viewModel.checkScenePhasesToRestart(newPhase)
        }
        .contextMenu {
            
            Button(action: { editViewModel.openEditSheet(countdown: viewModel.countdown) }) {
                Label("Edit \(viewModel.countdown.name)", systemImage: "pencil")
            }
            
            Button(action: { viewModel.delete() }) {
                Label("Delete \(viewModel.countdown.name)", systemImage: "trash")
            }
        }
        .fullScreenCover(isPresented: $editViewModel.isEditTimerSheet, onDismiss: {
            viewModel.resumeAppStart("onDismiss EditTimerView")
        }, content: {
            EditTimerView()
        })
    }
    
    @ViewBuilder func ControllNotRunning() -> some View {
        HStack(alignment: .bottom, spacing: 12) {
            Button(action: { viewModel.start() }) {
                Image(systemName: "play.circle.fill")
                    .font(.system(size: 48))
                    .foregroundStyle(theme.color(.textComplimentary))
            }
        }
    }
    
    @ViewBuilder func ControllRunning() -> some View {
        HStack(alignment: .bottom, spacing: 12) {
            Button(action: { viewModel.pause() }) {
                Image(systemName: "pause.circle.fill")
                    .font(.system(size: 48))
                    .foregroundStyle(theme.color(.textComplimentary))
            }
            
            Button(action: { viewModel.stop() }) {
                Image(systemName: "stop.circle.fill")
                    .font(.system(size: 48))
                    .foregroundStyle(theme.color(.textComplimentary))
            }
        }
    }
    
    @ViewBuilder func ControllResumeRunning() -> some View {
        HStack(alignment: .bottom, spacing: 12) {
            Button(action: { viewModel.resume() }) {
                Image(systemName: "play.circle.fill")
                    .font(.system(size: 48))
                    .foregroundStyle(theme.color(.textComplimentary))
            }
            
            Button(action: { viewModel.stop() }) {
                Image(systemName: "stop.circle.fill")
                    .font(.system(size: 48))
                    .foregroundStyle(theme.color(.textComplimentary))
            }
        }
    }
    
    @ViewBuilder func ContextMenu() -> some View {
        Menu {
            Text("Show this Timer on your Lock Screen and Dynamic Island.")
            
            Button {
                self.countdown.showAvtivity = true
                FirestoreTimerManager.shared.editTimer(countdown: self.countdown)
            } label: {
                Label("Show", systemImage: "bolt.fill")
            }
            
            Button {
                self.countdown.showAvtivity = false
                FirestoreTimerManager.shared.editTimer(countdown: self.countdown)
            } label: {
                Label("Don`t Show", systemImage: "bolt.slash")
            }
        } label: {
            HStack(spacing: 5) {
                Image(systemName: viewModel.countdown.showAvtivity ? "bolt.fill" : "bolt.slash")
                Text("Live Activity")
                Image(systemName: "chevron.down")
            }
            .foregroundStyle(theme.color(.textRegular))
            .padding(.vertical, 5)
            .padding(.horizontal, 10)
            .background {
                RoundedRectangle(cornerRadius: theme.cornerRadius)
                    .strokeBorder(theme.color(.textRegular), lineWidth: 1)
            }
        }
    }
}
