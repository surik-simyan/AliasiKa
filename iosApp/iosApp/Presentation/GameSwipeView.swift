import SwiftUI
import MultiPlatformLibrary
import MultiPlatformLibrarySwift
import Combine
import mokoMvvmFlowSwiftUI
import CardStack

struct GameSwipeView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @ObservedObject private var viewModel: GameViewModel
    private var wordSwipe: (LeftRight) -> Void
    @State private var playingTeamName: String = ""
    
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
        wordSwipe = { it in
            if it == LeftRight.right {
                viewModel.wordGuessed(index: nil)
            } else {
                viewModel.wordUnguessed()
            }
        }
        if(viewModel.playingTeam == GameViewModel.PlayingTeam.teamone) {
            playingTeamName = viewModel.teamOneName
        } else {
            playingTeamName = viewModel.teamTwoName
        }
    }
    
    
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
            
            VStack(alignment: .leading) {
                CardViewWithPadding {
                    HStack {
                        VStack{
                            Text(playingTeamName)
                                .font(.system(size: 24))
                            Text(String(viewModel.playingTeam == GameViewModel.PlayingTeam.teamone ? viewModel.state(\.teamOneScore) : viewModel.state(\.teamTwoScore)))
                                .font(.system(size: 32, weight: .bold))
                        }
                        .frame(minWidth: 0, maxWidth: .infinity)
                        Spacer()
                        VStack {
                            Text(SharedStrings.shared.gameTime.localized())
                                .font(.system(size: 24))
                            Text(viewModel.state(\.remainingTime))
                                .font(.system(size: 32, weight: .bold))
                        }
                        .frame(minWidth: 0, maxWidth: .infinity)
                    }
                }.fixedSize(horizontal: false, vertical: true)
                Spacer(minLength: 22)
                CardStack(
                    direction: LeftRight.direction,
                    data: viewModel.state(\.words) as [NSString],
                    onSwipe: { card, direction in
                        wordSwipe(direction)
                    },
                    content: { card, direction, isOnTop in
                        CardView{
                            Text(String(card as NSString))
                                .font(.system(size: 24))
                                .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                        }
                    }
                ).environment(\.cardStackConfiguration, CardStackConfiguration(
                    maxVisibleCards: 2,
                    swipeThreshold: 0.1,
                    cardOffset: 10,
                    cardScale: 0.2,
                    animation: .linear
                ))
                Spacer(minLength: 32)
                ZStack {
                    HStack {
                        Button(action: {
                            wordSwipe(LeftRight.left)
                        }) {
                            Text(SharedStrings.shared.gameWrong.localized())
                                .font(.system(size: 24))
                                .padding(.vertical, 16)
                                .foregroundColor(Color.white)
                                .frame(minWidth: 0, maxWidth: .infinity)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        .background(Color(UIColor(hex: ColorsKt.WrongButton)))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                        .frame(minWidth: 0, maxWidth: .infinity)
                        Spacer()
                        Button(action: {
                            wordSwipe(LeftRight.right)
                        }) {
                            Text(SharedStrings.shared.gameCorrect.localized())
                                .font(.system(size: 24))
                                .padding(.vertical, 16)
                                .foregroundColor(Color.white)
                                .frame(minWidth: 0, maxWidth: .infinity)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                        .background(Color(UIColor(hex: ColorsKt.RightButton)))
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                        .frame(minWidth: 0, maxWidth: .infinity)
                    }
                }.fixedSize(horizontal: false, vertical: true)
            }
            .padding(16)
        }
        .onAppear {
            if(viewModel.playingTeam == GameViewModel.PlayingTeam.teamone) {
                playingTeamName = viewModel.teamOneName
            } else {
                playingTeamName = viewModel.teamTwoName
            }
            viewModel.startTimer()
        }
        .onReceive(createPublisher(viewModel.actions)) { action in
            let actionKs = GameViewModelActionKs(action)
            switch(actionKs) {
            case .roundFinished:
                print("round finished")
                mode.wrappedValue.dismiss()
                break
            default:
                print("else")
                break
            }
        }
    }
}

struct GameSwipeView_Previews: PreviewProvider {
    static var previews: some View {
        GameSwipeView(viewModel: GameViewModel())
    }
}
