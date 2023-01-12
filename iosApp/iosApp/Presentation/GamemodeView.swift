import SwiftUI
import MultiPlatformLibrary

struct GamemodeView: View {
    private var viewModel = MainViewModelHelper().mainViewModel
    private let strengths = [
        SharedStrings.shared.gamemodeStandard.localized(),
        SharedStrings.shared.gamemodeSwipe.localized(),
        SharedStrings.shared.gamemodeStack.localized(),
    ]
    
    @State private var selectedStrength = SharedStrings.shared.gamemodeStandard.localized()
    @State private var gamemodeSelected = false
    
    
    var body: some View {
        VStack (spacing: 20) {
            Image("gamecontroller.fill")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 64.0, height: 64.0)
                .foregroundColor(Color(UIColor.primary))
            Text(SharedStrings.shared.playGamemode.localized())
            Picker("Strength", selection: $selectedStrength) {
                ForEach(strengths, id: \.self) {
                    Text($0)
                }
            }
            .scaledToFit()
            .pickerStyle(.wheel)
            .padding(.vertical, -20)
            NavigationLink(destination: PointsView(), isActive: $gamemodeSelected) {
                Button(action: {
                    switch selectedStrength {
                    case SharedStrings.shared.gamemodeStandard.localized():
                        viewModel.gamemode = MainViewModel.Gamemode.standard
                        break
                    case SharedStrings.shared.gamemodeSwipe.localized():
                        viewModel.gamemode = MainViewModel.Gamemode.swipe
                        break
                    case SharedStrings.shared.gamemodeStack.localized():
                        viewModel.gamemode = MainViewModel.Gamemode.stack
                        break
                    default:
                        viewModel.gamemode = MainViewModel.Gamemode.standard
                        break
                    }
                    gamemodeSelected = true
                }) {
                    Text("Next")
                        .font(.system(size: 18))
                        .padding()
                        .foregroundColor(.white)
                }
                .background(Color(UIColor.primary))
                .cornerRadius(CGFloat.dp(value: 4))
            }
        }
    }
}
