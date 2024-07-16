//
//  RegisterView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 10.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct RegisterView: View {
    @ObservedObject var viewModel = RegisterViewModel()
    @EnvironmentObject var authManager: FirebaseAuthManager
    @EnvironmentObject var alertManager: AlertManager
    @EnvironmentObject var theme: Theme
    
    var body: some View {
        ZStack {            
            backgroundImage()
            
            VStack(alignment: .center) {
                Spacer()
                
                Text("Hello!")
                    .font(.largeTitle)
                    .foregroundColor(theme.color(.textRegular))
                    .padding(.top, 20)
                
                Text("Create an account.")
                    .foregroundColor(theme.color(.textRegular))
                    .padding(.bottom, 20)
                
                VStack(alignment: .leading) {
  
                    TextField(text: $viewModel.firstName, label: { Text("Firstname:") })
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.vertical, 5)
                    
                    TextField(text: $viewModel.lastName, label: { Text("Lastname:") })
                        //.background(theme.color(.backgroundVariant))
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.vertical, 5)
                    
                    TextField(text: $viewModel.email, label: { Text("E-Mail:") })
                        //.background(theme.color(.backgroundVariant))
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.vertical, 5)
                        .keyboardType(.emailAddress)
                    
                    DatePicker(selection: $viewModel.surgeryDate, displayedComponents: .date, label: { Text("Surgery Date:") })
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .datePickerStyle(CompactDatePickerStyle())
                        .padding(.vertical, 5)
                    
                    SecureField(text: $viewModel.password, label: { Text("Password:") })
                        //.background(theme.color(.backgroundVariant))
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.vertical, 5)
                        .autocorrectionDisabled(true)
                        .textInputAutocapitalization(.never)
                    
                    SecureField(text: $viewModel.passwordVerify, label: { Text("Repeat password:") })
                        //.background(theme.color(.backgroundVariant))
                        .foregroundStyle(theme.color(.textRegular))
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding(.vertical, 5)
                        .autocorrectionDisabled(true)
                        .textInputAutocapitalization(.never)
                    
                }
                .padding(.horizontal, 10)
                
                HStack {
                    Spacer()
                    
                    Button(action: {
                        authManager.nav = .login
                    }, label: {
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
                        .padding(.top, 20)
                    })

                    Button(action: {
                        viewModel.signUp(complete: { error in
                            if (error != nil) {
                                alertManager.openAlert(error?.localizedDescription ?? "asds")
                            }
                        })
                    }) {
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


// TODO: REFECTOR
struct CustomShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()

        // Begin in the upper left corner
        path.move(to: CGPoint(x: 0, y: 0))

        // Draw a curve
        path.addQuadCurve(
            to: CGPoint(x: rect.width * 0.5, y: rect.height * 0.5),
            control: CGPoint(x: rect.width * 0.25, y: 0)
        )

        // Draw another curve to the bottom right
        path.addQuadCurve(
            to: CGPoint(x: rect.width, y: rect.height),
            control: CGPoint(x: rect.width * 0.75, y: rect.height)
        )

        // Draw the bottom edge
        path.addLine(to: CGPoint(x: rect.width, y: rect.height))

        // Draw the right edge
        path.addLine(to: CGPoint(x: rect.width, y: 0))

        // Close the path
        path.closeSubpath()

        return path
    }
}


#Preview("Light") {
    RegisterView()
        .environmentObject(Theme())
}

#Preview("Dark") {
    RegisterView()
        .environmentObject(Theme())
        .preferredColorScheme(.dark)
}
