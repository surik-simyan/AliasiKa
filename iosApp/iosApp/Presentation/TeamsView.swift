import SwiftUI
import MultiPlatformLibrary

struct TeamsView: View {
    private var viewModel = MainViewModelHelper().mainViewModel
    @State private var teamOne: String = ""
    @State private var teamTwo: String = ""
    @State private var teamsNamed = false
    
    var body: some View {
        VStack (spacing: 20) {
            Image("person.2.fill")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 64.0, height: 64.0)
                .foregroundColor(Color(UIColor.primary))
            Text(SharedStrings.shared.playTeams.localized())
            TextField("Team One", text: $teamOne)
                .padding(.horizontal)
                .textFieldStyle(.roundedBorder)
            TextField("Team Two", text: $teamTwo)
                .padding(.horizontal)
                .textFieldStyle(.roundedBorder)
            NavigationLink(destination: GameScoreView(), isActive: $teamsNamed) {
                Button(action: {
                    if(teamOne.isEmpty) {
                        viewModel.teamOneName = SharedStrings.shared.playTeamOne.localized()
                    } else {
                        viewModel.teamOneName = teamOne
                    }
                    if(teamTwo.isEmpty) {
                        viewModel.teamTwoName = SharedStrings.shared.playTeamTwo.localized()
                    } else {
                        viewModel.teamTwoName = teamTwo
                    }
                    teamsNamed = true
                }) {
                    Text(SharedStrings.shared.playStartGame.localized())
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
