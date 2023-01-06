import SwiftUI
import MultiPlatformLibrary
import CardStack

struct GameSwipeView: View {
    @ObservedObject private var viewModel: GameViewModel
    @State private var playingTeamScore: Int
    @State private var words: [String]
    private var wordSwipe: (LeftRight) -> Void
    private var playingTeamName: String
    
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
        wordSwipe = { it in
            if it == LeftRight.right {
                viewModel.wordGuessed(index: nil)
            } else {
                viewModel.wordUnguessed()
            }
        }
        words = viewModel.state(\.words)
        if(viewModel.playingTeam == GameViewModel.PlayingTeam.teamone) {
            playingTeamName = viewModel.teamOneName
            playingTeamScore = viewModel.state(\.teamOneScore)
        } else {
            playingTeamName = viewModel.teamTwoName
            playingTeamScore = viewModel.state(\.teamTwoScore)
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
                            Text(String($playingTeamScore.wrappedValue))
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
                Spacer()
                CardStack(
                    direction: LeftRight.direction,
                    data: words,
                    onSwipe: { card, direction in
                        wordSwipe(direction)
                        print("Swiped \(card) to \(direction)")
                    },
                    content: { card, direction, isOnTop in
                        CardView{
                            Text(card)
                                .font(.system(size: 24))
                                .frame(minWidth: 0, maxWidth: .infinity)
                        }
                    }
                ).environment(\.cardStackConfiguration, CardStackConfiguration(
                    maxVisibleCards: 2,
                    swipeThreshold: 0.1,
                    cardOffset: 40,
                    cardScale: 0.2,
                    animation: .linear
                  ))
                Spacer()
                HStack {
                    Button(action: {
                        
                    }) {
                        Text(SharedStrings.shared.gameWrong.localized())
                            .font(.system(size: 24))
                            .padding(.vertical, 16)
                            .background(Color(UIColor(hex: ColorsKt.WrongButton)))
                            .frame(minWidth: 0, maxWidth: .infinity)
                    }
                    .frame(minWidth: 0, maxWidth: .infinity)
                    Spacer()
                    Button(action: {
                        
                    }) {
                        Text(SharedStrings.shared.gameCorrect.localized())
                            .font(.system(size: 24))
                            .padding(.vertical, 16)
                            .background(Color(UIColor(hex: ColorsKt.WrongButton)))
                            .frame(minWidth: 0, maxWidth: .infinity)
                    }
                    .frame(minWidth: 0, maxWidth: .infinity)
                }
            }
            .padding(16)
        }.onAppear {
            viewModel.startTimer()
        }
    }
}

struct GameSwipeView_Previews: PreviewProvider {
    static var previews: some View {
        GameSwipeView(viewModel: GameViewModel())
    }
}
