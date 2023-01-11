import SwiftUI
import MultiPlatformLibrary

@main
struct iOSApp: App {
    init() {
        HelperKt.doInitKoin()
    }
    
    @State private var isActive : Bool = false

    var body: some Scene {
        WindowGroup {
            NavigationView {
                HomeView()
            }
            .navigationViewStyle(StackNavigationViewStyle())
            .environment(\.rootPresentationMode, self.$isActive)
            .accentColor(Color(UIColor.onSurface))
        }
    }
}

struct RootPresentationModeKey: EnvironmentKey {
    static let defaultValue: Binding<RootPresentationMode> = .constant(RootPresentationMode())
}

extension EnvironmentValues {
    var rootPresentationMode: Binding<RootPresentationMode> {
        get { return self[RootPresentationModeKey.self] }
        set { self[RootPresentationModeKey.self] = newValue }
    }
}

typealias RootPresentationMode = Bool

extension RootPresentationMode {
    
    public mutating func dismiss() {
        self.toggle()
    }
}
