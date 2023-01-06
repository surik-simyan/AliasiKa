import SwiftUI
import MultiPlatformLibrary

struct GamemodeView: View {
    
    private var viewModel: GameViewModel
    private let strengths = ["Standard", "Swipe", "Stack"]
    
    @State private var selectedStrength = "Standard"
    @State private var gamemodeSelected = false
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
    }
    
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
            NavigationLink(destination: PointsView(viewModel: self.viewModel), isActive: $gamemodeSelected) {
                Button(action: {
                    switch selectedStrength {
                    case "Standard":
                        viewModel.gamemode = GameViewModel.Gamemode.standard
                        break
                    case "Swipe":
                        viewModel.gamemode = GameViewModel.Gamemode.swipe
                        break
                    case "Stack":
                        viewModel.gamemode = GameViewModel.Gamemode.stack
                        break
                    default:
                        viewModel.gamemode = GameViewModel.Gamemode.standard
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

struct GamemodeView_Previews: PreviewProvider {
    static var previews: some View {
        GamemodeView(viewModel: GameViewModel())
    }
}
