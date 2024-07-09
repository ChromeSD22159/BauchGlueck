import SwiftUI
import Shared

struct ContentView: View {
    @State private var showContent = false
    var body: some View {
        VStack {
            Button("Click me!") {
                withAnimation {
                    showContent = !showContent
                }
            }

            if showContent {
                VStack(spacing: 16) {
                    Image(systemName: "swift")
                        .font(.system(size: 200))
                        .foregroundColor(.accentColor)
                    Text("SwiftUI: \(Greeting().greet())")
                }
                .transition(.move(edge: .top).combined(with: .opacity))
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

// kmp-observableviewmodel

// add packeges
/*
 Verwenden Sie Swift Package Manager, um Firebase-Abhängigkeiten zu installieren und zu verwalten.

 Öffnen Sie Ihr App-Projekt und gehen Sie in Xcode zu File > Add Packages (Datei > Pakete hinzufügen)
 Geben Sie die Repository-URL des Firebase iOS SDK ein, wenn Sie dazu aufgefordert werden:
 https://github.com/firebase/firebase-ios-sdk
 Wählen Sie die SDK-Version aus, die Sie verwenden möchten.
 Wir empfehlen die Standard-SDK-Version (neueste Version) zu verwenden. Bei Bedarf können Sie aber auch eine ältere Version verwenden.

 Wählen Sie die Firebase-Bibliotheken aus, die Sie verwenden möchten.
 Fügen Sie FirebaseAnalytics hinzu. Bei Analytics ohne Funktion für die IDFA-Erfassung fügen Sie stattdessen FirebaseAnalyticsWithoutAdId hinzu.

 Nachdem Sie auf Finish (Fertig) geklickt haben, löst Xcode die Abhängigkeiten automatisch auf und lädt sie im Hintergrund herunter.
 */

// add appDelegate
/*
 import SwiftUI
 import FirebaseCore


 class AppDelegate: NSObject, UIApplicationDelegate {
   func application(_ application: UIApplication,
                    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
     FirebaseApp.configure()

     return true
   }
 }

 @main
 struct YourApp: App {
   // register app delegate for Firebase setup
   @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate


   var body: some Scene {
     WindowGroup {
       NavigationView {
         ContentView()
       }
     }
   }
 }
 */
