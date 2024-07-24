//
//  ProfileEditView.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 17.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct ProfileEditView: View {
    @StateObject var viewModel: SettingViewModel

    var body: some View {
        Form {
            List {
                ChangeImage()
                
                PersonalData()
                
                MealsData()
                
                Section {
                    VStack(alignment: .leading) {
                        Text(
                            String(
                                format: NSLocalizedString(
                                    "Water intake per unit: %dml",
                                    comment: "Label for starting weight with placeholder"
                                ),
                                Int(viewModel.waterIntakeBinding.wrappedValue)
                            )
                        )
                        
                        Slider(
                            value: viewModel.waterIntakeBinding,
                            in: 50...1500,
                            step: 50,
                            label: {
                                Text(String(format: NSLocalizedString("Water intake per unit: %dml", comment: ""), viewModel.waterIntakeBinding.wrappedValue))
                            }
                        )
                    }
                    
                    VStack(alignment: .leading) {
                        Text(
                            String(
                                format: NSLocalizedString(
                                    "Water intake per Day: %dl",
                                    comment: "Label for starting weight with placeholder"
                                ),
                                Int(viewModel.waterDayIntakeBinding.wrappedValue)
                            )
                        )
                        
                        Slider(
                            value: viewModel.waterDayIntakeBinding,
                            in: 1000...4000,
                            step: 50,
                            label: {
                                Text(String(format: NSLocalizedString("Water intake per Day: %d", comment: ""), viewModel.waterDayIntakeBinding.wrappedValue))
                            }
                        )
                    }
                } header: {
                    Text("Water intake")
                }
            }
        }
    }
    
    @ViewBuilder func ChangeImage() -> some View {
        
        
        Section {
            HStack(spacing: 20) {

                if viewModel.authManager.userProfileImage.size != .zero {
                    Image(uiImage: viewModel.authManager.userProfileImage)
                            .resizable()
                            .cornerRadius(50)
                            .padding(.all, 4)
                            .frame(width: 100, height: 100)
                            .background(Theme().gradient(.primary))
                            .aspectRatio(contentMode: .fill)
                            .clipShape(Circle())
                            .padding(8)
                } else {
                    ZStack{
                        Color(.opaqueSeparator)
                        Text(viewModel.authManager.initials)
                            .font(.largeTitle)
                    }
                    .cornerRadius(50)
                    .padding(.all, 4)
                    .frame(width: 100, height: 100)
                    .background(Theme().gradient(.primary))
                    .aspectRatio(contentMode: .fill)
                    .clipShape(Circle())
                    .padding(8)
                }
                
                
                Button(action: {
                    viewModel.showImageSheet.toggle()
                }, label: {
                    Text("Change photo")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                            .background(Theme().gradient(.primary))
                            .cornerRadius(16)
                            .foregroundColor(.white)
                })
            }
            .sheet(isPresented: $viewModel.showImageSheet, onDismiss: loadImage) {
                ImagePicker(sourceType: .photoLibrary, selectedImage: $viewModel.authManager.userProfileImage)
            }
        } header: {
            Text("Profile Image")
        }
    }
    
    @ViewBuilder func PersonalData() -> some View {
        Section {
            HStack(spacing: 20) {
                Text("Firstname:")
                    .frame(width: .infinity, alignment: .leading)
                
                TextField("Max", text: viewModel.firstNameBinding)
                    .textFieldClearButton(text: viewModel.firstNameBinding)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }.frame(maxWidth: .infinity)
            
            HStack(spacing: 20) {
                Text("Lastname:")
                    .frame(width: .infinity, alignment: .leading)
                
                TextField("Mustermann", text: viewModel.lastNameBinding)
                    .textFieldClearButton(text: viewModel.lastNameBinding)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }.frame(maxWidth: .infinity)
            
            VStack(alignment: .leading) {
                Text(
                    String(
                        format: NSLocalizedString(
                            "Starting weight: %dkg",
                            comment: "Label for starting weight with placeholder"
                        ),
                        Int(viewModel.startWeigtBinding.wrappedValue)
                    )
                )
                
                Slider(
                    value: viewModel.startWeigtBinding,
                    in: 40...300,
                    step: 1,
                    label: {
                        Text(String(format: NSLocalizedString("Main meal: %d", comment: ""), viewModel.mainMealsBinding.wrappedValue))
                    }
                )
            }
        } header: {
            Text("Personal Data")
        }
        
    }
    
    @ViewBuilder func MealsData() -> some View {
        Section {
            
            Stepper(
                value: viewModel.mainMealsBinding,
                in: 3...10,
                step: 1,
                label: {
                    Text(String(format: NSLocalizedString("Main meal: %d", comment: ""), viewModel.mainMealsBinding.wrappedValue))
                }
            )
            
            Stepper(
                value: viewModel.betweenMealsBinding,
                in: 3...10,
                step: 1,
                label: {
                    Text(String(format: NSLocalizedString("Between meals: %d", comment: ""), viewModel.betweenMealsBinding.wrappedValue))
                }
            )
            
            Text(String(format: NSLocalizedString("Total number of meals: %d", comment: ""), viewModel.betweenMealsBinding.wrappedValue + viewModel.mainMealsBinding.wrappedValue))
        } header: {
            Text("Meals")
        }
    }
    
    func loadImage() {
        viewModel.authManager.uploadAndSaveProfileImage(uiImage: viewModel.authManager.userProfileImage) { result in
            switch result {
            case .success:
                print("Profile image uploaded and saved successfully!")
            case .failure(let error):
                print("Error uploading profile image: \(error.localizedDescription)")
            }
        }
    }
}
