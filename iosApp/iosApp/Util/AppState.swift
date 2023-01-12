import SwiftUI
import Combine

class AppState: ObservableObject {
    @Published var moveToHome: Bool = false
}
