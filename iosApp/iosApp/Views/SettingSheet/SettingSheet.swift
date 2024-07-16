//
//  SettingSheet.swift
//  BauchGlück
//
//  Created by Frederik Kohler on 15.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import FirebaseFirestore
import FirebaseAuth
import Firebase
import SwiftUI
import StoreKit

struct SettingSheet: ViewModifier {
    @EnvironmentObject var theme: Theme
    @EnvironmentObject var authManager: FirebaseAuthManager
    @Environment(\.requestReview) var requestReview
    
    var isSettingSheet: Binding<Bool>
    @StateObject var viewModel: SettingViewModel
    
    init(isSettingSheet: Binding<Bool>, authManager: FirebaseAuthManager) {
        self.isSettingSheet = isSettingSheet
        _viewModel = StateObject(wrappedValue: SettingViewModel(authManager: authManager))
    }

    func body(content: Content) -> some View {
        content
            .sheet(isPresented: isSettingSheet, onDismiss: {
                viewModel.updateUserProfileUser()
            }, content: {
                NavigationView {
                    ZStack {
                        theme.color(.backgroundVariant).ignoresSafeArea()
                        
                        List {
                            Section{
                                Text(viewModel.greeting)
                            }
                            
                            TimeSinceSurgeryBadge()
                            
                            
                            Section {
                                NavigationLink {
                                    ProfileEditView(viewModel: viewModel)
                                        .navigationBackButton(color: theme.color(.textRegular), text: "Settings")
                                } label: {
                                    RowItem(icon: "person.fill", text: "Profile")
                                        .listRowBackground(theme.color(.backgroundVariant))
                                }
       
                                RowItem(image: .stromach, text: "Operiert seit:", surgeryDateBinding: viewModel.surgeryDateBinding)
                                
                            } header: {
                                Text("Profile")
                            }
                            
                            Section{
                                RowItem(icon: "envelope", text: "Support + Feedback", url: "https://www.instagram.com/frederik.code/")
                                RowItem(icon: "star.fill", text: "Rate 5 stars", action: { requestReview() })
                            } header: {
                                Text("Support")
                            }
                            
                            Section{
                                RowItem(icon: "globe", text: "Instagram Entwicklers", url: "https://www.instagram.com/frederik.code/")
                           
                                RowItem(icon: "globe", text: "Website des Entwicklers", url: "https://www.appsbyfrederikkohler.de")
                                
                                RowItem(icon: "square.grid.2x2.fill", text: "Apps des Entwicklers", url: "https://apps.apple.com/at/developer/frederik-kohler/id1692240999")
                                
                                RowItem(icon: "info.circle", text: "Version \(Bundle.main.infoDictionary!["CFBundleShortVersionString"] ?? "1.0")")
                            } header: {
                                Text("Entwickler")
                            }
                            
                            RowItem(icon: "iphone.and.arrow.forward", text: "Sign out", action: { viewModel.authManager.signOut() })
                                .listRowBackground(theme.gradient(.primary))
                                .foregroundStyle(theme.color(.textComplimentary))
                        }
                    }
                    .navigationTitle("⚙️ Settings")
                    .navigationBarTitleDisplayMode(.inline)
                }
            })
    }
    
    @ViewBuilder func TimeSinceSurgeryBadge() -> some View {
        Section {
            HStack(alignment: .center, spacing: 10) {
                Image(.stromach)
                    .font(.largeTitle)
                
                VStack(alignment: .leading, spacing: 5) {
                    Text("Unglaublich, wie die Zeit vergeht!")
                    Text(viewModel.timeSinceSurgery)
                }
                .font(.callout)
            }
            .foregroundStyle(theme.color(.textComplimentary))
            .padding(.vertical, 10)
        }
        .listRowBackground(theme.gradient(.primary))
        
    }
}

struct RowItem: View {
    var icon: String?
    var image: ImageResource?
    var text: String
    var url: String?
    var type: RowItem.RowItemType
    var surgeryDateBinding: Binding<Date>?
    var action: () -> Void
    var toggle: Binding<Bool>?
    
    @EnvironmentObject var theme: Theme
    
    init(icon: String? = "", image: ImageResource? = nil, text: String) {
        self.icon = icon
        self.text = text
        self.image = image
        self.url = nil
        self.type = RowItem.RowItemType.text
        self.action = {}
        self.toggle = nil
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: String, url: String) {
        self.icon = icon
        self.text = text
        self.image = image
        self.url = url
        self.type = RowItem.RowItemType.link
        self.action = {}
        self.toggle = nil
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: String, surgeryDateBinding: Binding<Date>) {
        self.icon = icon
        self.image = image
        self.text = text
        self.type = RowItem.RowItemType.datePicker
        self.surgeryDateBinding = surgeryDateBinding
        self.action = {}
        self.toggle = nil
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: String, action: @escaping () -> Void) {
        self.icon = icon
        self.image = image
        self.text = text
        self.type = RowItem.RowItemType.button
        self.action = action
        self.toggle = nil
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: String, toggle: Binding<Bool>) {
        self.icon = icon
        self.image = image
        self.text = text
        self.type = RowItem.RowItemType.toggle
        self.action = {}
        self.toggle = toggle
    }
    
    enum RowItemType {
        case text, link, datePicker, button, toggle
    }
    
    var body: some View {
        Button(action: action ) {
            HStack {
                if let icon = icon {
                    BackgroundCircleIcon(icon: icon)
                } else if let image = image {
                    BackgroundCircleImage(image: image)
                }
               
                switch type {
                    case .text: Text(text)
                    case .link: Link(text, destination: URL(string: url ?? "")!)
                    case .datePicker: if let surgeryDateBinding = surgeryDateBinding {
                        DatePicker("Operiert seit:", selection: surgeryDateBinding , displayedComponents: .date)
                    }
                    case .button: Text(text).foregroundStyle(theme.color(.textComplimentary))
                    case .toggle: Toggle(isOn: toggle ?? .constant(false), label: {}).labelsHidden()
                }
            }
        }
        .foregroundStyle(theme.color(.textRegular))
        .font(.callout)
        .padding(.vertical, 5)
    }
    
    @ViewBuilder func BackgroundCircleIcon(icon:String) -> some View {
        ZStack {
            Circle()
                .fill(theme.gradient(.primary))
                .frame(width: 30, height: 30)
            
            Image(systemName: icon)
                .padding(10)
                .foregroundStyle(theme.color(.textComplimentary))
        }
    }
    
    @ViewBuilder func BackgroundCircleImage(image: ImageResource) -> some View {
        ZStack {
            Circle()
                .fill(theme.gradient(.primary))
                .frame(width: 30, height: 30)
            
            Image(image)
                .padding(10)
                .foregroundStyle(theme.color(.textComplimentary))
        }
    }
}

struct ProfileEditView: View {
    @StateObject var viewModel: SettingViewModel

    var body: some View {
        Form {
            List {
                ChangeImage()
                
                PersonalData()
                
                MealsData()
            }
        }
    }
    
    @ViewBuilder func ChangeImage() -> some View {
        Section {
            HStack(spacing: 20) {
                Image(uiImage: viewModel.authManager.userProfileImage)
                        .resizable()
                        .cornerRadius(50)
                        .padding(.all, 4)
                        .frame(width: 100, height: 100)
                        .background(Theme().gradient(.primary)) // Color.black.opacity(0.2)
                        .aspectRatio(contentMode: .fill)
                        .clipShape(Circle())
                        .padding(8)
                
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
                Spacer()
                TextField("Firstname", text: viewModel.firstNameBinding)
                    .textFieldClearButton(text: viewModel.firstNameBinding)
                    .multilineTextAlignment(.leading)
            }
            
            HStack(spacing: 20) {
                Text("Lastname:")
                Spacer()
                TextField("Lastname", text: viewModel.lastNameBinding)
                    .textFieldClearButton(text: viewModel.lastNameBinding)
            }
        } header: {
            Text("Personal Data")
        }
    }
    
    @ViewBuilder func MealsData() -> some View {
        Section {
            Stepper("Main meal:  \(viewModel.mainMealsBinding.wrappedValue)",
                    value: viewModel.mainMealsBinding,
                    in: 3...10,
                    step: 1)
            
            Stepper("Between meals:  \(viewModel.betweenMealsBinding.wrappedValue)",
                    value: viewModel.betweenMealsBinding,
                    in: 3...10,
                    step: 1)
            
            Text("Total number of meals: \(viewModel.betweenMealsBinding.wrappedValue + viewModel.mainMealsBinding.wrappedValue)")
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


