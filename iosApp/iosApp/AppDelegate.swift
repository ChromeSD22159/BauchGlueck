//
//  AppDelegate.swift
//  iosApp
//
//  Created by Frederik Kohler on 09.07.24.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import FirebaseCore
import Firebase
import Shared

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        let providerFactory = MySimpleAppCheckProviderFactory()
        AppCheck.setAppCheckProviderFactory(providerFactory)
        
        FirebaseApp.configure()

        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        //Auth.auth().setAPNSToken(deviceToken, type: .unknown)
        
        Messaging.messaging().apnsToken = deviceToken
        
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        let token = tokenParts.joined()
        print("Device Token: \(token)")
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.banner, .sound])
    }

    /*
    func applicationDidEnterBackground(_ application: UIApplication) {
        let request = BGAppRefreshTaskRequest(identifier: "com.example.app.refresh")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60) // 15 minutes from now

        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Failed to schedule background refresh: \(error)")
        }
    }

    func handleAppRefreshTask(_ task: BGAppRefreshTask) {
        // Perform data synchronization
        syncData()

        task.setTaskCompleted(success: true)
    }
    */
}

class MySimpleAppCheckProviderFactory: NSObject, AppCheckProviderFactory {
  func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
    return AppAttestProvider(app: app)
  }
}

/*
1.1 Synchronisation beim Öffnen der App
    - Android: MainActivity onCreate
    - iOS: AppDelegate didFinishLaunchingWithOptions -> syncData()

1.2 Synchronisation beim Schließen der App
    - Android: MainActivity onDestroy()
    - iOS: AppDelegate applicationWillEnterForeground oder BGAppRefreshTaskRequest -> syncData()

2.
Datenmodell mit Room

Synchronisationslogik mit WorkManager

Shared: ApiService
ApiService ist ein Interface, das die API-Endpunkte definiert. Es beschreibt, wie die App mit dem Backend-Server kommuniziert, z.B. durch HTTP-Requests.

Shared: ApiServiceImpl
ApiServiceImpl ist eine Implementierung des ApiService-Interfaces. Hier wird die tatsächliche Logik für die Kommunikation mit dem Backend implementiert, oft unter Verwendung von Retrofit oder einer ähnlichen HTTP-Bibliothek.

Shared: RecipeRepository
RecipeRepository ist ein Interface, das die Methoden zur Verwaltung von Rezeptdaten definiert. Es agiert als Abstraktionsschicht zwischen der Datenquelle (API oder lokale Datenbank) und der Anwendungslogik.

androidMain: AppDatabase
AppDatabase ist die Room-Datenbankklasse, die die lokalen Datenbanktabellen definiert und DAOs bereitstellt. Es ist die zentrale Klasse für den Zugriff auf die lokale Datenbank.

androidMain: RecipeRepositoryImpl
RecipeRepositoryImpl ist die konkrete Implementierung des RecipeRepository-Interfaces. Es verwendet ApiService und AppDatabase für die Datenverwaltung und Implementierung der Logik zum Abrufen, Hinzufügen, Aktualisieren und Löschen von Rezepten.

androidMain: SyncDataWorker
SyncDataWorker ist eine Implementierung von Worker, die verwendet wird, um Hintergrundaufgaben wie die Synchronisation von Daten mit einem Server durchzuführen. Es wird verwendet, um regelmäßige Synchronisationen oder einmalige Aufgaben zu planen.


*/
