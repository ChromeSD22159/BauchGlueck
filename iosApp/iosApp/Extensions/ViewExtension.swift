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

            Image(systemName: dynamicImage)
                .foregroundColor(dynamicColor)
            
        }
    }
}

#Preview("SheetLight") {
    ZStack{
        
    }
    .settingSheet(isSettingSheet: .constant(true), authManager: FirebaseAuthManager())
    .environmentObject(Theme())
    .preferredColorScheme(.dark)
}

#Preview("SheetDark") {
    ZStack{
        
    }
    .settingSheet(isSettingSheet: .constant(true), authManager: FirebaseAuthManager())
    .environmentObject(Theme())
    .preferredColorScheme(.light)
}
