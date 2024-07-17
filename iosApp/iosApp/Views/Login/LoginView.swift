//
//  Login.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct LoginView: View {
    @ObservedObject var viewModel = LoginViewModel()
    @EnvironmentObject var authManager: FirebaseAuthManager
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var alertManager: AlertManager

    var body: some View {
        ZStack {
            BackgroundImage()

            VStack(alignment: .center) {
                Spacer()

                Text("Welcome Back!")
                    .font(.kodchasanBold(size: .largeTitle))
                    .foregroundColor(theme.color(.secondary))
                    .padding(.top, 20)

                Text("Log in to your account.") // Changed text
                    .foregroundColor(theme.color(.textRegular))
                    .padding(.bottom, 20)

                VStack(alignment: .leading) {
                    TextField(text: $viewModel.email, label: { Text("E-Mail:") })
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .keyboardType(.emailAddress)
                        .textInputAutocapitalization(.never)
                        .padding(.vertical, 5)

                    SecureField(text: $viewModel.password, label: { Text("Password:") })
                        .foregroundStyle(theme.color(.textRegular))
                        .keyboardType(.default)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.vertical, 5)
                }
                .padding(.horizontal, 10)

                HStack {
                    Spacer()
                    
                    NavigationLink(destination: RegisterView()) {
                        
                    }
                    
                    Button(action: {
                        authManager.nav = .signUp
                    }, label: {
                        HStack {
                            Text("To register")
                            
                            Image(systemName: "person.crop.circle.fill.badge.plus")
                               
                        }
                        .padding(.horizontal, theme.paddingHorizontal)
                        .padding(.vertical,  theme.paddingVertical)
                        .foregroundColor(.white)
                        .clipShape(
                            .rect(
                                topLeadingRadius: theme.cornerRadius,
                                bottomLeadingRadius: theme.cornerRadius,
                                bottomTrailingRadius: theme.cornerRadius,
                                topTrailingRadius: theme.cornerRadius
                            )
                        )
                        .padding(.top, 20)
                    })
                    
                    
                    
                    Button(action: {
                        viewModel.login(complete: { error in
                            if (error != nil) {
                                alertManager.openAlert(error?.localizedDescription ?? "asds")
                            }
                        })
                    }) {
                        HStack {
                            Text("Log in")
                            
                            Image(systemName: "person.fill.checkmark")
                               
                        }
                        .padding(.horizontal, theme.paddingHorizontal)
                        .padding(.vertical,  theme.paddingVertical)
                        .background(
                            theme.gradient(array: [theme.color(.primary), theme.color(.primaryVariant)])
                        )
                        .foregroundColor(.white)
                        .clipShape(
                            .rect(
                                topLeadingRadius: theme.cornerRadius,
                                bottomLeadingRadius: theme.cornerRadius,
                                bottomTrailingRadius: theme.cornerRadius,
                                topTrailingRadius: theme.cornerRadius
                            )
                        )
                    }
                    .padding(.top, 20)
                }

                Spacer()
            }
            .padding(.horizontal, theme.paddingHorizontal)
        }
    }
}

#Preview("Light") {
    LoginView()
        .environmentObject(Theme())
}

#Preview("Dark") {
    LoginView()
        .environmentObject(Theme())
        .preferredColorScheme(.dark)
}
