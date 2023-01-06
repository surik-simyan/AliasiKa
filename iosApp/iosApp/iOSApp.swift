import SwiftUI
import MultiPlatformLibrary

@main
struct iOSApp: App {
    init() {
        HelperKt.doInitKoin()
    }
    
    private let viewModel = GameViewModelHelper().getViewModel()
    
    
    var body: some Scene {
        WindowGroup {
            NavigationView {
                HomeView(viewModel: viewModel)
            }.accentColor(Color(UIColor.onSurface))
        }
    }
}
