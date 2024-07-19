//
//  Timer.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 18.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Combine
import FirebaseFirestore

struct CountDownTimer: Identifiable, Codable {
    var id: String
    var userId: String
    var name: String
    var duration: Int
    var startDate: Date?
    var endDate: Date?
    var timerState: String
    var timerType: String
    var remainingDuration: Int
}

struct TimerOverView: View {
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var auth: FirebaseAuthManager
    @EnvironmentObject var firestoreTimerManager: FirestoreTimerManager
    @Environment(\.scenePhase) var scenePhase
    var body: some View {
        ZStack {
            theme.color(.backgroundVariant).ignoresSafeArea()
      
            ScrollView{
                VStack(alignment: .leading, spacing: 16) {
                    ForEach(firestoreTimerManager.timerList) { timer in
                        TimerCard(countdown: timer)
                            .padding(.horizontal, 16)
                    }
                }
            }
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    HStack {
                        RoundedHeaderButton(icon: "arrow.triangle.2.circlepath") {
                            if let user = auth.user {
                                FirestoreTimerManager.shared.synronize(userId: user.uid)
                            }
                        }
                    }
                }
            }
        }
    }
    
    @ViewBuilder func RoundedHeaderButton(icon: String, action: @escaping () -> Void) -> some View {
        Button(action: { action() }) {
            ZStack {
                theme.gradient(array: [theme.color(.primary), theme.color(.primaryVariant)])
                .frame(width: 33, height: 33)
                .clipShape(Circle())
                
                Image(systemName: icon)
                    .font(Font.custom("SF Pro", size: 16))
                    .foregroundColor(.white)
                    .padding(8)
                
            }
        }
    }
}

class CountdownViewModel: ObservableObject {
    @Published var countdown: CountDownTimer
    @Published var countdownCopy: CountDownTimer
    @State var ticker = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    var calcDurationPercent: Int {
        if countdown.remainingDuration <= 0 {
            return 100
        } else {
            let percent = (countdown.remainingDuration * 100) / countdown.duration
            return percent
        }
    }
    
    init(countdown: CountDownTimer) {
        self.countdown = countdown
        self.countdownCopy = countdown
    }

    func start() {
        countdown.timerState = TimerState.running.rawValue
        countdown.startDate = Date()
        countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.duration))
        startTicker()
        objectWillChange.send()
        
        saveCountdownToUserDefaults()
    }

    func stop() {
        countdown.timerState = TimerState.notRunning.rawValue
        countdown.startDate = nil
        countdown.endDate = nil
        countdown.remainingDuration = countdownCopy.remainingDuration
        stopTicker()
        objectWillChange.send()
        
        removeCountdownFromUserDefaults()
    }

    func pause() {
        stopTicker()
        countdown.timerState = TimerState.paused.rawValue
        objectWillChange.send()
        saveCountdownToUserDefaults()
    }
    
    func resume() {
        if countdown.timerState == TimerState.paused.rawValue {
            countdown.startDate = Date()
            countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.remainingDuration))
        } else {
            countdown.startDate = Date()
            countdown.endDate = Date().addingTimeInterval(TimeInterval(countdown.duration))
        }
        countdown.timerState = TimerState.running.rawValue
        startTicker()
        objectWillChange.send()
        saveCountdownToUserDefaults()
    }
    
    func resumeAppStart() {
        if let savedState = loadCountdownFromUserDefaults() {
            self.countdown = savedState
            
            if self.countdown.timerState == TimerState.running.rawValue {
                let now = Date()
                
                if let end = countdown.endDate {
                    let differenceSeconds = Int(end.timeIntervalSince(now))
                    
                    if differenceSeconds > 0 {
                        countdown.remainingDuration = differenceSeconds
                        countdown.startDate = now
                        startTicker()
                    } else {
                        stop()
                    }
                }
            }
        }
    }

    func updateTimer() {
        guard countdown.timerState == TimerState.running.rawValue else { return }
        
        if let end = countdown.endDate {

            let differenceSecondsInteger = Int(end.timeIntervalSince(Date()))
            
            if differenceSecondsInteger > 0 {
                countdown.remainingDuration = differenceSecondsInteger
            } else {
                stop()
            }
            
        }
        
        objectWillChange.send()
    }
    
    func calcRectangleWidth(geometryReader: GeometryProxy) -> CGFloat {
        let totalWidth = geometryReader.size.width
        return (CGFloat(calcDurationPercent) / 100.0) * totalWidth
    }

    var formatTime: String {
        let sec = countdown.remainingDuration
        let minutes = (sec % 3600) / 60
        let seconds = sec % 60
        return String(format: "%02d:%02d", minutes, seconds)
    }
    
    func saveCountdownToUserDefaults() {
        let defaults = UserDefaults.standard
        if let encoded = try? JSONEncoder().encode(countdown) {
            defaults.set(encoded, forKey: countdown.id)
        }
    }
        
    private func removeCountdownFromUserDefaults() {
        let defaults = UserDefaults.standard
        defaults.removeObject(forKey: countdown.id)
    }
        
    func loadCountdownFromUserDefaults() -> CountDownTimer? {
        let defaults = UserDefaults.standard
        if let savedCountdown = defaults.object(forKey: countdown.id) as? Data {
            if let loadedCountdown = try? JSONDecoder().decode(CountDownTimer.self, from: savedCountdown) {
                return loadedCountdown
            }
        }
        return nil
    }
    
    private func startTicker() {
        ticker = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    }

    func stopTicker() {
        ticker = Timer.publish(every: 0, on: .main, in: .common).autoconnect()
    }
    
    func checkScenePhasesToRestart(_ newPhase: ScenePhase) {
        if newPhase == .active {
            self.resumeAppStart()
        } else if newPhase == .background {
            self.stopTicker()
        }
    }
    
    func delete() {
        FirestoreTimerManager.shared.deleteTimer(countdown: countdown)
    }
}

struct TimerCard: View {
    @EnvironmentObject var theme: Theme
    @Environment(\.scenePhase) var scenePhase
    @StateObject var viewModel: CountdownViewModel
    @State var countdown: CountDownTimer

    @StateObject var editViewModel = EditTimerViewModel.shared
    
    init(countdown: CountDownTimer) {
        self._countdown = State(wrappedValue: countdown)
        self._viewModel = StateObject(wrappedValue: CountdownViewModel(countdown: countdown))
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
            viewModel.updateTimer()
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
}

struct LinkCard: View {
    @EnvironmentObject var theme: Theme
    var name: String
    var color: Color

    init(name: String, explicitColor: Color) {
        self.name = name
        self.color = explicitColor
    }
    
    var body: some View {
        ZStack {
            color
            
            VStack {
                Spacer(minLength: 75)
                
                HStack {
                    Spacer()
                    
                    Text(name)
                        .font(.kodchasanBold(size: .title))
                        .foregroundStyle(theme.color(.textComplimentary))
                }
            }
            .padding(16)
        }
        .cornerRadius(16)
    }
}
