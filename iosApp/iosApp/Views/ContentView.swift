//
//  ContentView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation

import SwiftUI

struct ContentView: View {
    @StateObject var authManager = FirebaseAuthManager()
    
    @Environment(\.colorScheme) var colorScheme
    
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
        }
        .onChange(of: colorScheme) { newScheme in
            theme.changeTheme(newScheme)
        }
        .environmentObject(authManager)
        .environmentObject(theme)
    }
}


