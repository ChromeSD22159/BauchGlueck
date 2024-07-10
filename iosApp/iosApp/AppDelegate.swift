//
//  AppDelegate.swift
//  iosApp
//
//  Created by Frederik Kohler on 09.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import Firebase

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

        AppCheck.setAppCheckProviderFactory(AppCheckDebugProviderFactory())
      
        FirebaseApp.configure()

        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
       Auth.auth().setAPNSToken(deviceToken, type: .unknown)
     }
}
