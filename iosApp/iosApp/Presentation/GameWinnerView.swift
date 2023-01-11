import SwiftUI
import MultiPlatformLibrary

struct GameWinnerView: View {
    @Environment(\.rootPresentationMode) private var rootPresentationMode: Binding<RootPresentationMode>
    private var viewModel = MainViewModelHelper().mainViewModel
    
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
            
            VStack(alignment: .center) {
                Text(viewModel.gameWinnerText().localized())
                    .font(.system(size: 72))
                    .foregroundColor(Color(UIColor.onSurface))
                    .multilineTextAlignment(.center)
            }.onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
                    viewModel.resetValues()
                    self.rootPresentationMode.wrappedValue.dismiss()
                    print("poped to root")
                }
            }
        }
        .navigationBarBackButtonHidden(true)
    }
}


