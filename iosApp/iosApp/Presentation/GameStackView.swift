import SwiftUI
import Foundation
import MultiPlatformLibrary
import MultiPlatformLibrarySwift
import mokoMvvmFlowSwiftUI
import Combine

struct GameStackView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @ObservedObject private var viewModel: GameViewModel
    private var wordClick: (Int32) -> Void
    @State private var playingTeamName: String = ""
    
    init(viewModel: GameViewModel) {
        self.viewModel = viewModel
        wordClick = { it in
            viewModel.wordGuessed(index: KotlinInt(int: it))
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
                    ScrollView(showsIndicators: false) {
                        VStack(alignment: .leading) {
                            ForEach((viewModel.state(\.stackWords) as [NSString]).enumerated().map { IndexedString(index: $0, string: $1) }, id: \.index) { indexedString in
                                StackWord(text: String(indexedString.string)) {
                                    wordClick(Int32(indexedString.index))
                                }
                            }
                        }
                    }
                    .scrollEnabled(false)
                }
                .ignoresSafeArea(.all, edges: .bottom)
            }
            .padding(16)
            .ignoresSafeArea(.all, edges: .bottom)
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

struct IndexedString {
    let index: Int
    let string: NSString
}

struct GameStackView_Previews: PreviewProvider {
    static var previews: some View {
        GameStackView(viewModel: GameViewModel())
    }
}
