//
//  Login.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import Shared
import SwiftUI

struct LoginView: View {
    @ObservedObject var viewModel: LoginViewModel = LoginViewModel()
    
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
                    TextField("Mail", text: viewModel.binding(\.mail))
                        .textContentType(.username)
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .keyboardType(.emailAddress)
                        .textInputAutocapitalization(.never)
                        .padding(.vertical, 5)

                    SecureField("Password", text: viewModel.binding(\.password))
                        .textContentType(.password) 
                        .foregroundStyle(theme.color(.textRegular))
                        .keyboardType(.default)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.vertical, 5)
                }
                .padding(.horizontal, 10)

                if viewModel.state(\.isProcessing) {
                    ProgressView()
                } else {
                    HStack {
                        Spacer()
                        
                        NavigationLink(destination: RegisterView()) {
                            
                        }
                        
                        Button(action: {
                            FirebaseAuthManager.shared.navigateTo(view: .signup)
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
                            login()
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
                }

                Spacer()
            }
            .padding(.horizontal, theme.paddingHorizontal)
        }
        .onReceive(createPublisher(viewModel.actions), perform: { action in
            switch(action) {
            case is LoginViewModel.ActionLoginSuccess:
                //Navigate to HomeScreen
                //showAlert = true
                break
            default:
                break
            }
        })
        .onAppear {
            viewModel.isProcessing.subscribe { state in
                if let isProcessingState = state {
                    //isProcessing = state as! Bool
                }
            }

        }
    }
    
    func login() {
        viewModel.onLoginButtonPressed { loginState in

            FirebaseAuthManager.shared.signIn(email: loginState.mail, password: loginState.password) { result in
                
                switch result {
                    case .success(let authResult):
                        alertManager.openAlert("Login Successful")
                        loginState.isSignedIn = true
                    case .failure(let error):
                        alertManager.openAlert(error.localizedDescription)
                        loginState.isSignedIn = false
                }
                
            }
            
            return loginState
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
