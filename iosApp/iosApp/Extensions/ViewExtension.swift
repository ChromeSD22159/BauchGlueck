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
    
    func navigationBackButton(color: Color, icon: String? = nil, text: String? = nil) -> some View {
       modifier(NavigationBackButton(color: color, text: text))
   }
}

struct NavigationBackButton: ViewModifier {

    @Environment(\.presentationMode) var presentationMode
    var color: Color
    var icon: String?
    var text: String?

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

            Image(systemName: dynamicImage)
                .foregroundColor(dynamicColor)
            
        }
    }
}

#Preview("BackButton") {
    NavigationView {
        NavigationLink {
            HStack {
                Text("Hallo")
            }
            .navigationBackButton(color: Theme().color(.textRegular), text: "Settings")
        } label: {
            HStack {
                Text("Hallo")
            }
            .listRowBackground(Theme().color(.backgroundVariant))
        }
        .navigationTitle("⚙️ Settings")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview("Sheet") {
    ZStack{
        
    }
    .settingSheet(isSettingSheet: .constant(true), authManager: FirebaseAuthManager())
    .environmentObject(Theme())
}