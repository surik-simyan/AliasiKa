import SwiftUI
import MultiPlatformLibrary
import MultiPlatformLibrarySwift
import mokoMvvmFlowSwiftUI
import Combine

struct GameStandardView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @State private var showAlertDialog = false
    @ObservedObject private var viewModel = StandardGameViewModelHelper().standardGameViewModel
    @State private var wordsStates = [false, false, false, false, false]
    @State private var playingTeamName: String = ""
    
    var body: some View {
        Group {
            ZStack {
                Color(UIColor.secondary)
                    .ignoresSafeArea()
                
                VStack(alignment: .leading) {
                    CardViewWithPadding {
                        HStack {
                            VStack{
                                Text(playingTeamName)
                                    .font(.system(size: 24))
                                Text(String(viewModel.state(\.score)))
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
                            Word(text: viewModel.state(\.standardWords)[0], wordClicked: $wordsStates[0], onWordClick: {onWordClick(state: $wordsStates[0])})
                            Word(text: viewModel.state(\.standardWords)[1], wordClicked: $wordsStates[1], onWordClick: {onWordClick(state: $wordsStates[1])})
                            Word(text: viewModel.state(\.standardWords)[2], wordClicked: $wordsStates[2], onWordClick: {onWordClick(state: $wordsStates[2])})
                            Word(text: viewModel.state(\.standardWords)[3], wordClicked: $wordsStates[3], onWordClick: {onWordClick(state: $wordsStates[3])})
                            Word(text: viewModel.state(\.standardWords)[4], wordClicked: $wordsStates[4], onWordClick: {onWordClick(state: $wordsStates[4])})
                        }
                    }.fixedSize(horizontal: false, vertical: true)
                    Spacer()
                }
                .padding(16)
            }
            .onAppear {
                playingTeamName = viewModel.playingTeamName
            }
            .navigationBarBackButtonHidden(true)
            .navigationBarItems(leading: Button(action : {
                viewModel.pauseTimer()
                showAlertDialog = true
            }){
                Image(systemName: "arrow.left")
            })
            .alert(isPresented: $showAlertDialog) {
                Alert(
                    title: Text(SharedStrings.shared.gameFinishRoundTitle.localized()),
                    message: Text(SharedStrings.shared.gameFinishRoundMessage.localized()),
                    primaryButton: Alert.Button.default(Text(SharedStrings.shared.gameFinishPositive.localized()), action: { viewModel.finishRoundEarly() }),
                    secondaryButton: .cancel(Text(SharedStrings.shared.gameFinishNegative.localized()), action: { viewModel.resumeTimer() }))
            }
            .onReceive(createPublisher(viewModel.actions)) { action in
                let actionKs = AbstractGameViewModelActionKs(action)
                switch(actionKs) {
                case .roundFinished:
                    print("round finished")
                    mode.wrappedValue.dismiss()
                    break
                case .fiveWordsGuessed:
                    wordsStates = wordsStates.map { _ in false }
                    break
                }
            }
        }
    }
    
    func onWordClick(state: Binding<Bool>) -> Void {
        state.wrappedValue = !state.wrappedValue
        if state.wrappedValue {
            viewModel.wordGuessed(index: nil)
        } else {
            viewModel.wordUnguessed()
        }
    }
}

struct Word: View {
    let text: String
    var wordClicked: Binding<Bool>
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
        .background(wordClicked.wrappedValue ? Color(UIColor(hex: ColorsKt.Guessed)) : Color(UIColor(hex: Int64(ColorsKt.Unguessed))).opacity(0))
    }
}
