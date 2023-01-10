import SwiftUI
import MultiPlatformLibrary

@main
struct iOSApp: App {
    init() {
        HelperKt.doInitKoin()
    }
    
    @State var isActive : Bool = false
    
    var body: some Scene {
        WindowGroup {
            NavigationView {
                HomeView(rootIsActive: self.$isActive)
            }
            .accentColor(Color(UIColor.onSurface))
        }
    }
}
