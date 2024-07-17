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
    @StateObject var authManager = FirebaseAuthManager()
    @StateObject var alertManager = AlertManager()
    var notificationManager = NotificationManager()
    
    @Environment(\.colorScheme) var colorScheme
    @Environment(\.scenePhase) var scenePhase
    
    @StateObject var theme = Theme()
  
    var body: some View {
        ZStack {
            switch authManager.nav {
                case .logged: NavigationStack { HomeView() }
                case .login: LoginView()
                case .signUp: RegisterView()
            }
        }
        
        .onAppear {
            theme.changeTheme(colorScheme)
            notificationManager.requestPermisson()
        }
        .onChange(of: colorScheme) { newScheme in
            theme.changeTheme(newScheme)
        }
        .alert(alertManager.message, isPresented: $alertManager.presentAlert, actions: {
            Button("Ok") {
                alertManager.closeAlert()
            }
        })
        .environmentObject(authManager)
        .environmentObject(alertManager)
        .environmentObject(theme)
    }
    
    init() {
        printFonts(false)
    }
    
    private func printFonts(_ bool: Bool) {
        if bool {
            for familyName in UIFont.familyNames {
                print(familyName)
                
                for fontName in UIFont.fontNames(forFamilyName: familyName) {
                    print("-- \(fontName)")
                }
            }
        }
    }
}


