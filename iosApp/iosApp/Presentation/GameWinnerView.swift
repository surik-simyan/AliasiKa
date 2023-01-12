import SwiftUI
import MultiPlatformLibrary

struct GameWinnerView: View {
    @EnvironmentObject var appState: AppState
    private var viewModel = MainViewModelHelper().mainViewModel
    
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
            
            VStack(alignment: .center) {
                Text(viewModel.gameWinnerText().localized())
                    .font(.system(size: 72))
                    .foregroundColor(Color.white)
                    .multilineTextAlignment(.center)
            }.onAppear {
                DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
                    self.appState.moveToHome = true
                }
            }
        }
        .navigationBarBackButtonHidden(true)
    }
}


