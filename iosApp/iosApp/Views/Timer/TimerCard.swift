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
        self._countdown = State(wrappedValue: countdown)
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
                        Text(viewModel.formatTime)
                            .font(.kodchasanBold(size: 52))
                            .foregroundStyle(theme.color(.textComplimentary))
                            .monospacedDigit()
                        Text(viewModel.countdown.name)
                            .font(.kodchasanBold(size: .title))
                            .foregroundStyle(theme.color(.textComplimentary))
                    }
                }
            }
            .padding(16)
        }
        .frame(maxHeight: 200)
        .cornerRadius(16)
        .onAppear(perform: {
            viewModel.resumeAppStart()
        })
        .onDisappear(perform: {
            viewModel.saveCountdownToUserDefaults()
        })
        .onChange(of: scenePhase) { newPhase in
            viewModel.checkScenePhasesToRestart(newPhase)
        }
        .onReceive(viewModel.ticker, perform: { value in
            viewModel.updateTimer(sendNote: false)
        })
        .onChange(of: viewModel.countdown.duration, perform: { newDuration in
            print(newDuration)
        })
        .contextMenu {
            /*
            Button(action: { editViewModel.openEditSheet() }) {
                Label("Edit \(viewModel.countdown.name)", systemImage: "pencil")
            }
            */
            
            Button(action: { viewModel.delete() }) {
                Label("Delete \(viewModel.countdown.name)", systemImage: "trash")
            }
        }
        .fullScreenCover(isPresented: $editViewModel.isEditTimerSheet, onDismiss: {}, content: {
            EditTimerView(countdown: countdown)
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
            
            Button(action: { viewModel.stop(sendNote: false) }) {
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
}
