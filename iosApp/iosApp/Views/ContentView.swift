//
//  ContentView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import FirebaseAuth

struct ContentView: View {
    @StateObject var authManager = FirebaseAuthManager.shared
    @StateObject var alertManager = AlertManager()
    @StateObject var notificationManager = NotificationManager.shared
    @StateObject var healthManager = HealthManager.shared
    @StateObject var themeManager = Theme()
    @StateObject var timerManager = FirestoreTimerManager.shared
    
    var body: some View {
           ZStack {
               switch authManager.nav {
                   case .logged: NavigationStack { HomeView() }
                   case .login: LoginView()
                   case .signUp: RegisterView()
               }
           }
           .appear(notificationManager: notificationManager, healthManager: healthManager, timerManager: timerManager)
           .onChangeScene(healthManager: healthManager)
           .onChangeColorScheme()
           .alert(alertManager.message, isPresented: $alertManager.presentAlert, actions: {
               Button("Ok") {
                   alertManager.closeAlert()
               }
           })
           .environmentObject(authManager)
           .environmentObject(alertManager)
           .environmentObject(themeManager)
           .environmentObject(timerManager)
       }
}
