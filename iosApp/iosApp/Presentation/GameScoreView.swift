import SwiftUI
import MultiPlatformLibrary
import MultiPlatformLibrarySwift
import mokoMvvmFlowSwiftUI
import Combine

struct GameScoreView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    private var viewModel: GameViewModel
    private var isShowing = true
    @State private var showAlertDialog = false
    @State private var teamOneScore = 0
    @State private var teamTwoScore = 0
    
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
        teamOneScore = viewModel.teamOneScore.value!.intValue
        teamTwoScore = viewModel.teamTwoScore.value!.intValue
    }
    
    
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
            
            VStack(alignment: .leading) {
                CardViewWithPadding {
                    HStack {
                        VStack{
                            Text(viewModel.teamOneName)
                                .font(.system(size: 24))
                            Text(String(teamOneScore))
                                .font(.system(size: 32, weight: .bold))
                        }
                        .frame(minWidth: 0, maxWidth: .infinity)
                        Spacer()
                        VStack {
                            Text(viewModel.teamTwoName)
                                .font(.system(size: 24))
                            Text(String(teamTwoScore))
                                .font(.system(size: 32, weight: .bold))
                        }
                        .frame(minWidth: 0, maxWidth: .infinity)
                    }
                }.fixedSize(horizontal: false, vertical: true)
                Spacer()
                NavigationLink(destination: getDestination(from: viewModel.gamemode)) {
                    CardViewWithPadding {
                        VStack{
                            Text(viewModel.playingTeam == GameViewModel.PlayingTeam.teamone ? viewModel.teamOneName : viewModel.teamTwoName)
                                .font(.system(size: 48))
                            Text(SharedStrings.shared.gameStart.localized())
                                .font(.system(size: 48, weight: .bold))
                        }
                        .padding(.vertical, CGFloat.dp(value: 24))
                        
                    }.fixedSize(horizontal: false, vertical: true)
                }
                Spacer()
            }
            .padding(16)
            .onAppear {
                teamOneScore = viewModel.teamOneScore.value!.intValue
                teamTwoScore = viewModel.teamTwoScore.value!.intValue
            }
        }
        .onDisappear {
            viewModel
        }
        .navigationBarBackButtonHidden(true)
        .navigationBarItems(leading: Button(action : {
            showAlertDialog = true
        }){
            Image(systemName: "arrow.left")
        })
        .alert(isPresented: $showAlertDialog) {
            Alert(
                title: Text(SharedStrings.shared.gameFinishTitle.localized()),
                message: Text(SharedStrings.shared.gameFinishMessage.localized()),
                primaryButton: Alert.Button.default(Text(SharedStrings.shared.gameFinishPositive.localized()), action: { viewModel.endGameEarly() }),
                secondaryButton: .cancel(Text(SharedStrings.shared.gameFinishNegative.localized())))
        }
        .onReceive(createPublisher(viewModel.actions)) { action in
            let actionKs = GameViewModelActionKs(action)
            switch(actionKs) {
            case .gameEnded:
                print("game ended")
                break
            default:
                print("score")
                print(actionKs)
                break
            }
        }
    }
    
    func getDestination(from gamemode: GameViewModel.Gamemode) -> AnyView {
        switch gamemode {
        case .standard:
            return AnyView(GameStandardView(viewModel: self.viewModel))
        case .swipe:
            return AnyView(GameSwipeView(viewModel: self.viewModel))
        case .stack:
            return AnyView(GameStackView(viewModel: self.viewModel))
        default:
            return AnyView(GameStandardView(viewModel: self.viewModel))
        }
    }
}

struct GameScoreView_Previews: PreviewProvider {
    static var previews: some View {
        GameScoreView(viewModel: GameViewModel())
    }
}
