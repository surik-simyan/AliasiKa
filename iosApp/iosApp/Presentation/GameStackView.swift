import SwiftUI
import Foundation
import MultiPlatformLibrary
import mokoMvvmFlowSwiftUI
import Combine

struct GameStackView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @ObservedObject private var viewModel = StackGameViewModelHelper().stackGameViewModel
    @State private var showAlertDialog = false
    @State private var playingTeamName: String = ""
    
    
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
            
            VStack(alignment: .leading) {
                CardViewWithPadding {
                    HStack {
                        VStack{
                            Text(viewModel.playingTeamName)
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
                    ScrollView(showsIndicators: false) {
                        VStack(alignment: .leading) {
                            ForEach((viewModel.state(\.stackWords) as [NSString]).enumerated().map { IndexedString(index: $0, string: $1) }, id: \.index) { indexedString in
                                StackWord(text: String(indexedString.string)) {
                                    onWordClick(index: Int32(indexedString.index))
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
                mode.wrappedValue.dismiss()
                break
            default:
                break
            }
        }
        .ignoresSafeArea(.all, edges: .bottom)
    }
    
    func onWordClick(index: Int32) -> Void {
        viewModel.wordGuessed(index: KotlinInt(int: index))
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
