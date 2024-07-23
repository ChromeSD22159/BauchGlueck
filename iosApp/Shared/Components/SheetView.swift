//
//  SheetView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 23.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct SheetView<Content>: View where Content: View {
    @EnvironmentObject var theme: Theme
    @Binding var close: Bool
    @State var sheetName: LocalizedStringKey
    let content: () -> Content
    let closeFunc: () -> Void
    let addFunc: () -> Void
    let editFunc: () -> Void
    let buttonType: ButtonType
    
    init(sheetName: LocalizedStringKey, closeBinding: Binding<Bool>, @ViewBuilder content: @escaping () -> Content, closeFunc: @escaping () -> Void, addFunc: @escaping () -> Void) {
        self._close = closeBinding
        self.sheetName = sheetName
        self.content = content
        self.closeFunc = closeFunc
        self.addFunc = addFunc
        self.editFunc = {}
        self.buttonType = ButtonType.add
    }
    
    init(sheetName: LocalizedStringKey, closeBinding: Binding<Bool>, @ViewBuilder content: @escaping () -> Content, closeFunc: @escaping () -> Void, editFunc: @escaping () -> Void) {
        self._close = closeBinding
        self.sheetName = sheetName
        self.content = content
        self.closeFunc = closeFunc
        self.editFunc = editFunc
        self.addFunc = {}
        self.buttonType = ButtonType.edit
    }
    
    var body: some View {
        ZStack {
            BackgroundImage()
            VStack(spacing: 16) {
                Header()
                
                content()
                
                switch buttonType {
                case .add: addCancelControl()
                case .edit: editCancelControl()
                }
                
            }
            .padding(16)
        }
    }
    
    @ViewBuilder func Header() -> some View {
        HStack {
            Text(sheetName)
                .foregroundStyle(theme.color(.primary))
                .font(.kodchasanBold(size: .title3))
            
            Spacer()
            
            Button {
                close.toggle()
            } label: {
                HStack {
                    Image(systemName: "xmark")
                }.foregroundStyle(theme.color(.textRegular))
            }
        }.padding(.bottom, 100)
    }
    
    @ViewBuilder func addCancelControl() -> some View {
        HStack(spacing: 8) {
            Button {
                close.toggle()
                closeFunc()
            } label: {
                HStack {
                    Text("Cancel")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Theme().gradient(.primary))
                        .cornerRadius(16)
                        .foregroundColor(.white)
                }
                .background(
                    RoundedRectangle(cornerRadius: theme.cornerRadius)
                        .fill(theme.gradient(.primary))
                )
            }
            
            Button {
                close.toggle()
                addFunc()
            } label: {
                HStack {
                    Text("Save")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Theme().gradient(.primary))
                        .cornerRadius(16)
                        .foregroundColor(.white)
                }
                .background(
                    RoundedRectangle(cornerRadius: theme.cornerRadius)
                        .fill(theme.gradient(.primary))
                )
            }
        }
    }
    
    @ViewBuilder func editCancelControl() -> some View {
        HStack(spacing: 8) {
            Button {
                close.toggle()
                closeFunc()
            } label: {
                HStack {
                    Text("Cancel")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Theme().gradient(.primary))
                        .cornerRadius(16)
                        .foregroundColor(.white)
                }
                .background(
                    RoundedRectangle(cornerRadius: theme.cornerRadius)
                        .fill(theme.gradient(.primary))
                )
            }
            
            Button {
                close.toggle()
                editFunc()
            } label: {
                HStack {
                    Text("Save")
                        .font(.headline)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Theme().gradient(.primary))
                        .cornerRadius(16)
                        .foregroundColor(.white)
                }
                .background(
                    RoundedRectangle(cornerRadius: theme.cornerRadius)
                        .fill(theme.gradient(.primary))
                )
            }
        }
    }
    
    enum ButtonType {
        case add, edit
    }
}
