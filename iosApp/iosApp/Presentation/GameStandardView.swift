import SwiftUI
import MultiPlatformLibrary
import mokoMvvmFlowSwiftUI
import Combine

struct GameStandardView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @State private var showingAlert = false
    @ObservedObject private var viewModel: GameViewModel
    @State private var wordsStates = [false, false, false, false, false]
    private var wordClick: (Binding<Bool>) -> Void
    private var playingTeamName: String
    
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
        wordClick = { it in
            it.wrappedValue = !it.wrappedValue
            if it.wrappedValue {
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
                Spacer()
                CardView {
                    VStack(spacing: 0) {
                        Word(text: viewModel.state(\.fiveWords)[0], wordClicked: $wordsStates[0], onWordClick: wordClick)
                        Word(text: viewModel.state(\.fiveWords)[1], wordClicked: $wordsStates[1], onWordClick: wordClick)
                        Word(text: viewModel.state(\.fiveWords)[2], wordClicked: $wordsStates[2], onWordClick: wordClick)
                        Word(text: viewModel.state(\.fiveWords)[3], wordClicked: $wordsStates[3], onWordClick: wordClick)
                        Word(text: viewModel.state(\.fiveWords)[4], wordClicked: $wordsStates[4], onWordClick: wordClick)
                    }
                }.fixedSize(horizontal: false, vertical: true)
                Spacer()
            }
            .padding(16)
            .onReceive(createPublisher(viewModel.actions)) { action in
                print("swift action")
            }
        }
        .onAppear {
            viewModel.startTimer()
        }
//        .onReceive(createPublisher(viewModel.actions)) { action in
//            print("swift action")
//        }
    }
}

struct Word: View {
    let text: String
    var wordClicked: Binding<Bool>
    let onWordClick: (Binding<Bool>) -> Void
    
    var body: some View {
        Button(action: {
            onWordClick(wordClicked)
        }) {
            Text(text)
                .font(.system(size: 24))
                .padding(.vertical, 16)
                .frame(minWidth: 0, maxWidth: .infinity)
        }
        .frame(minWidth: 0, maxWidth: .infinity)
        .background(wordClicked.wrappedValue ? Color(UIColor(hex: ColorsKt.Guessed)) : Color(UIColor(hex: Int64(ColorsKt.Unguessed))).opacity(0))
    }
}

struct GameStandardView_Previews: PreviewProvider {
    static var previews: some View {
        GameStandardView(viewModel: GameViewModel())
    }
}
