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

#Preview {
    TimerOverView()
}
