//
//  ViewExtension.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 15.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

extension View {
    func settingSheet(isSettingSheet: Binding<Bool>, authManager: FirebaseAuthManager) -> some View {
        modifier(SettingSheet(isSettingSheet: isSettingSheet, authManager: authManager))
    }
    
    func textFieldClearButton(text: Binding<String>) -> some View {
        modifier(TextFieldClearButton(text: text))
    }
    
    func navigationBackButton(color: Color, icon: String? = nil, text: LocalizedStringKey? = nil) -> some View {
       modifier(NavigationBackButton(color: color, text: text))
    }
    
    func appear(notificationManager: NotificationManager, healthManager: HealthManager, timerManager: FirestoreTimerManager) -> some View {
        modifier(AppearModifier(notificationManager: notificationManager, healthManager: healthManager, timerManager: timerManager))
    }
    
    func onChangeScene(healthManager: HealthManager) -> some View {
        modifier(OnChangeSceneModifier(healthManager: healthManager))
    }
    
    func onChangeColorScheme() -> some View {
        modifier(OnChangeColorSchemeModifier())
    }
}

struct NavigationBackButton: ViewModifier {

    @Environment(\.presentationMode) var presentationMode
    var color: Color
    var icon: String?
    var text: LocalizedStringKey?

    func body(content: Content) -> some View {
        return content
            .navigationBarBackButtonHidden(true)
            .navigationBarItems( leading: 
                HStack(spacing: 16) {
                    Image(systemName: icon ?? "arrow.backward")
                    .font(.body)
                    if let text = text {
                        Text(text)
                            .font(.callout)
                    }
                }
                .onTapGesture {
                    presentationMode.wrappedValue.dismiss()
                }
            )
    }
}

struct TextFieldClearButton: ViewModifier {
    @Binding var text: String
    
    @State private var iconName: String = "xmark.seal.fill"
    
    private var isValidTxt: Bool {
        text.count >= 3
    }
    
    private var dynamicImage: String {
        isValidTxt ? "checkmark.seal.fill" : "xmark.seal.fill"
    }
    
    private var dynamicColor: Color {
        isValidTxt ? Theme().color(.primary) : Color(UIColor.opaqueSeparator)
    }
    
    func body(content: Content) -> some View {
        HStack {
            content
                
            Spacer()
            
            Image(systemName: dynamicImage)
                .foregroundColor(dynamicColor)
            
        }
    }
}

struct AppearModifier: ViewModifier {
    @EnvironmentObject var theme: Theme
    @Environment(\.colorScheme) var colorScheme
    
    var notificationManager: NotificationManager
    var healthManager: HealthManager
    var timerManager: FirestoreTimerManager
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                theme.changeTheme(colorScheme)
                
                notificationManager.requestPermisson()
                healthManager.requestAuthorization()
                healthManager.fetchLists(days: 7)
                
                Task {
                    timerManager.initialize(loadLokal: false)
                }
            }
    }
}

struct OnChangeSceneModifier: ViewModifier {
    @Environment(\.scenePhase) var scenePhase
    var healthManager: HealthManager
    
    func body(content: Content) -> some View {
        content
            .onChange(of: scenePhase) { value in
                switch value {
                    case .background: return
                    case .inactive: return
                    case .active: healthManager.fetchLists(days: 7)
                    @unknown default: return
                }
            }
    }
}

struct OnChangeColorSchemeModifier: ViewModifier {
    @EnvironmentObject var theme: Theme
    @Environment(\.colorScheme) var colorScheme
    
    func body(content: Content) -> some View {
        content
            .onChange(of: colorScheme) { newScheme in
                theme.changeTheme(newScheme)
            }
    }
}
