import SwiftUI
import Foundation
import MultiPlatformLibrary

struct GameStackView: View {
    @ObservedObject private var viewModel: GameViewModel
    @State private var playingTeamScore: Int
    private var wordClick: (Int32) -> Void
    private var playingTeamName: String
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
        wordClick = { it in
            viewModel.wordGuessed(index: KotlinInt(int: it))
        }
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
                Spacer()
                CardView {
                    ScrollView(showsIndicators: false) {
                        VStack(alignment: .leading) {
                            ForEach(viewModel.state(\.words), id: \.element) {
                                StackWord(text:\($0)) {
                                    wordClick(Int32(index))
                                }
                            }
                        }
                    }.scrollEnabled(false)
                }
                .ignoresSafeArea(.all, edges: .bottom)
            }
            .padding(16)
            .ignoresSafeArea(.all, edges: .bottom)
        }
        .onAppear {
            viewModel.startTimer()
        }
        .ignoresSafeArea(.all, edges: .bottom)
        
    }
}

struct StackWord: View {
    let text: String
    let onWordClick: () -> Void
    
    var body: some View {
        Button(action: {
            onWordClick()
        }) {
            Text(text)
                .font(.system(size: 24))
                .padding(.vertical, 16)
                .frame(minWidth: 0, maxWidth: .infinity)
        }
        .frame(minWidth: 0, maxWidth: .infinity)
        
        
    }
}

struct GameStackView_Previews: PreviewProvider {
    static var previews: some View {
        GameStackView(viewModel: GameViewModel())
    }
}
