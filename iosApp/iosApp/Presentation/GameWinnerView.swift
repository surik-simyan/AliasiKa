import SwiftUI
import MultiPlatformLibrary

struct GameWinnerView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    private var viewModel = MainViewModelHelper().mainViewModel
    
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
        }
    }
}


