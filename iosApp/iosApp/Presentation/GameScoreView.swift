import SwiftUI
import MultiPlatformLibrary
import MultiPlatformLibrarySwift
import mokoMvvmFlowSwiftUI
import Combine

struct GameScoreView: View {
    @Environment(\.presentationMode) var mode: Binding<PresentationMode>
    private var viewModel = MainViewModelHelper().mainViewModel
    private var isShowing = true
    @State private var isGameEnded = false
    @State private var showAlertDialog = false
    
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
            
            NavigationLink(destination: NavigationLazyView(GameWinnerView()), isActive: $isGameEnded) {
                VStack(alignment: .leading) {
                    CardViewWithPadding {
                        HStack {
                            VStack{
                                Text(viewModel.teamOneName)
                                    .font(.system(size: 24))
                                Text(String(viewModel.state(\.teamOneScore)))
                                    .font(.system(size: 32, weight: .bold))
                            }
                            .frame(minWidth: 0, maxWidth: .infinity)
                            Spacer()
                            VStack {
                                Text(viewModel.teamTwoName)
                                    .font(.system(size: 24))
                                Text(String(viewModel.state(\.teamTwoScore)))
                                    .font(.system(size: 32, weight: .bold))
                            }
                            .frame(minWidth: 0, maxWidth: .infinity)
                        }
                    }.fixedSize(horizontal: false, vertical: true)
                    Spacer()
                    NavigationLink(destination: NavigationLazyView(getDestination(from: viewModel.gamemode))) {
                        CardViewWithPadding {
                            VStack{
                                Text(viewModel.state(\.playingTeamName))
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
                    print("on appread called")
                    isGameEnded = viewModel.isGameEnded()
                    print(isGameEnded)
                }
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
                    primaryButton: Alert.Button.default(Text(SharedStrings.shared.gameFinishPositive.localized()), action: {
                        viewModel.endGameEarly()
                        isGameEnded = true
                    }),
                    secondaryButton: .cancel(Text(SharedStrings.shared.gameFinishNegative.localized())))
            }
        }
    }
    
    func getDestination(from gamemode: MainViewModel.Gamemode) -> AnyView {
        switch gamemode {
        case .standard:
            return AnyView(GameStandardView())
        case .swipe:
            return AnyView(GameSwipeView())
        case .stack:
            return AnyView(GameStackView())
        default:
            return AnyView(GameStandardView())
        }
    }
}
