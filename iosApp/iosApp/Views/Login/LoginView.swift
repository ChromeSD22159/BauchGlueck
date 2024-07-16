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
            backgroundImage() // Reusing for branding consistency

            VStack(alignment: .center) {
                Spacer()

                Text("Welcome Back!") // Changed text
                    .font(.largeTitle)
                    .foregroundColor(theme.color(.textRegular))
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

                    // SecureField for password input
                    SecureField(text: $viewModel.password, label: { Text("Password:") })
                        .foregroundStyle(theme.color(.textRegular))
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
                            Text("Sign up")
                            
                            Image(systemName: "person.crop.circle.fill")
                               
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
                            
                            Image(systemName: "person.crop.circle.fill")
                               
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

    @ViewBuilder func backgroundImage() -> some View {
        VStack(alignment: .trailing) {
            HStack(alignment: .top) {
                Spacer()
                ZStack(alignment: .topTrailing) {
                    
                    Image(.waveBehinde)
                        .opacity(0.3)
                        .frame(width: 266.15442, height: 283.81583, alignment: .topTrailing)
                    
                    Image(.waveAbove)
                        .opacity(0.3)
                        .frame(width: 266.15442, height: 283.81583, alignment: .topTrailing)

                    Image(.logoTransparent)
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                        .frame(width: 140, height: 140)
                        .padding(.top, 80)
                        .padding(.trailing, 30)
                        .clipped()
               }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(theme.color(.background))
        .edgesIgnoringSafeArea(.all)
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

