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
                            
                            TimeSinceSurgeryBadge()
                            
                            
                            Section {
                                NavigationLink {
                                    ProfileEditView(viewModel: viewModel)
                                        .navigationBackButton(color: theme.color(.textRegular), text: "Settings")
                                } label: {
                                    RowItem(icon: "person.fill", text: "Profile")
                                        .listRowBackground(theme.color(.backgroundVariant))
                                }
       
                                RowItem(image: .stromach, text: "Bypass since:", surgeryDateBinding: viewModel.surgeryDateBinding)
                                
                            } header: {
                                Text("Profile")
                            }
                            
                            Section{ 
                                RowItem(icon: "envelope", text: "Support + Feedback", url: "https://www.instagram.com/frederik.code/")
                                RowItem(icon: "star.fill", text: "Rate 5 stars", action: { requestReview() }, background: .regular)
                                    .foregroundStyle(theme.color(.textRegular))
                            } header: {
                                Text("Support")
                            }
                            
                            Section { 
                                RowItem(icon: "globe", text: "Developer's Instagram", url: "https://www.instagram.com/frederik.code/")
                           
                                RowItem(icon: "globe", text: "Developer's website", url: "https://www.appsbyfrederikkohler.de")
                                
                                RowItem(icon: "square.grid.2x2.fill", text: "Developer's apps", url: "https://apps.apple.com/at/developer/frederik-kohler/id1692240999")
                                
                                let version = Bundle.main.infoDictionary!["CFBundleShortVersionString"] as! String
                                let build = Bundle.main.infoDictionary?["CFBundleVersion"] as! String
                                let string = "Version \(version) (Build Number: \(build))"
                                RowItem(icon: "info.circle", text: LocalizedStringKey(string))
                            } header: {
                                Text("Developer")
                            }
                            
                            RowItem(icon: "iphone.and.arrow.forward", text: "Sign out", action: { viewModel.authManager.signOut() }, background: .backgroundGradient)
                                .listRowBackground(theme.gradient(.primary))
                                
                        }
                    }
                    .navigationTitle("⚙️ Settings")
                    .navigationBarTitleDisplayMode(.inline)
                }
            })
    }
    
    @ViewBuilder func TimeSinceSurgeryBadge() -> some View {
        Section {
            VStack {
                HStack(alignment: .top, spacing: 10) {
                    Image(.stromach)
                        .font(.largeTitle)
                    
                    VStack(alignment: .leading, spacing: 5) {
                        Text(viewModel.greeting)
                            .font(.kodchasanBold(size: .title3))
                        
                        Text("Unbelieveable how the time goes by!")
                        Text(viewModel.timeSinceSurgery)
                    }
                    .font(.callout)
                    .frame(maxWidth: .infinity)
                }
                .padding(.vertical, 10)
            }
        }
        .foregroundStyle(theme.color(.textComplimentary))
        .listRowBackground(theme.gradient(.primary))
        
    }
}

struct RowItem: View {
    var icon: String?
    var image: ImageResource?
    var text: LocalizedStringKey
    var url: String?
    var type: RowItem.RowItemType
    var fill: RowItem.RowItemFill
    var surgeryDateBinding: Binding<Date>?
    var action: () -> Void
    var toggle: Binding<Bool>?
    
    @EnvironmentObject var theme: Theme
    
    init(icon: String? = "", image: ImageResource? = nil, text: LocalizedStringKey) {
        self.icon = icon
        self.text = text
        self.image = image
        self.url = nil
        self.type = RowItem.RowItemType.text
        self.action = {}
        self.toggle = nil
        self.fill = .regular
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: LocalizedStringKey, url: String) {
        self.icon = icon
        self.text = text
        self.image = image
        self.url = url
        self.type = RowItem.RowItemType.link
        self.action = {}
        self.toggle = nil
        self.fill = .regular
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: LocalizedStringKey, surgeryDateBinding: Binding<Date>) {
        self.icon = icon
        self.image = image
        self.text = text
        self.type = RowItem.RowItemType.datePicker
        self.surgeryDateBinding = surgeryDateBinding
        self.action = {}
        self.toggle = nil
        self.fill = .regular
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: LocalizedStringKey, action: @escaping () -> Void, background: RowItem.RowItemFill) {
        self.icon = icon
        self.image = image
        self.text = text
        self.type = RowItem.RowItemType.button
        self.action = action
        self.toggle = nil
        self.fill = background
    }
    
    init(icon: String? = nil, image: ImageResource? = nil, text: LocalizedStringKey, toggle: Binding<Bool>) {
        self.icon = icon
        self.image = image
        self.text = text
        self.type = RowItem.RowItemType.toggle
        self.action = {}
        self.toggle = toggle
        self.fill = .regular
    }
    
    enum RowItemType {
        case text, link, datePicker, button, toggle
    }
    
    enum RowItemFill {
        case regular, backgroundGradient
    }
    
    var body: some View {
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
                case .button: Text(text).onTapGesture { action() }
                case .toggle: Toggle(isOn: toggle ?? .constant(false), label: {}).labelsHidden()
            }
        }
        .foregroundStyle(fill == .regular ? theme.color(.textRegular) : theme.color(.textComplimentary))
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
