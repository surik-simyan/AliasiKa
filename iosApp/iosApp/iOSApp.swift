import SwiftUI
import MultiPlatformLibrary

@main
struct iOSApp: App {
    
    init() {
        HelperKt.doInitKoin()
    }
    
    let appState = AppState()
    
    var body: some Scene {
        WindowGroup {
            HomeView()
                .environmentObject(appState)
        }
    }
}
