import SwiftUI
import MultiPlatformLibrary
import Combine
import mokoMvvmFlowSwiftUI
import CardStack

struct GameSwipeView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    @ObservedObject private var viewModel = SwipeGameViewModelHelper().swipeGameViewModel
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
                Spacer(minLength: 22)
                CardStack(
                    direction: LeftRight.direction,
                    data: viewModel.state(\.swipeWords) as [NSString],
                    onSwipe: { card, direction in
                        onWordSwipe(direction: direction)
                    },
                    content: { card, direction, isOnTop in
                        CardView{
                            Text(String(card as NSString))
                                .font(.system(size: 24))
                                .frame(minWidth: 0, maxWidth: .infinity, minHeight: 0, maxHeight: .infinity)
                        }
                    }
                )
                .allowsHitTesting(false)
                Spacer(minLength: 32)
                ZStack {
                    HStack {
                        Button(action: {
                            onWordSwipe(direction: LeftRight.left)
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
                            onWordSwipe(direction: LeftRight.right)
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
    }
    
    func onWordSwipe(direction: LeftRight) -> Void {
        if direction == LeftRight.right {
            viewModel.wordGuessed(index: nil)
        } else {
            viewModel.wordUnguessed()
        }
    }
}
