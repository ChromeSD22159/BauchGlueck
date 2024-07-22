//
//  TimerOverView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 20.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct TimerOverView: View {
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var auth: FirebaseAuthManager
    @Environment(\.scenePhase) var scenePhase
    
    @ObservedObject var firestoreTimerManager = FirestoreTimerManager.shared
    var body: some View {
        ZStack {
            theme.color(.background).ignoresSafeArea()
            
            theme.backgroundImageWithOutImage(
                background: .backgroundVariant,
                backgroundOpacity: 1
            )
      
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
                            FirestoreTimerManager.shared.synronize()
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

#Preview {
    TimerOverView()
        .environmentObject(Theme())
        .environmentObject(FirebaseAuthManager())
}
