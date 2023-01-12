import SwiftUI
import MultiPlatformLibrary

struct HomeView: View {
    
    @EnvironmentObject var appState: AppState
    @State var isView1Active: Bool = false

    
    var body: some View {
        NavigationView {
            ZStack {
                Color(UIColor.secondary)
                    .ignoresSafeArea()
                
                VStack {
                    NavigationLink(destination: GamemodeView(), isActive: self.$isView1Active) {
                        Text(SharedStrings.shared.play.localized())
                            .font(.system(size: 48, weight: .regular))
                            .foregroundColor(Color(UIColor.onSurface))
                            .frame(minWidth: 0, maxWidth: .infinity)
                            .padding(.vertical, CGFloat.dp(value: 24))
                        
                    }
                    .isDetailLink(false)
                    .frame(minWidth: 0, maxWidth: .infinity)
                    .background(Color(UIColor.surface))
                    .cornerRadius(CGFloat.dp(value: 4))
                    .padding()
                }
                .onReceive(self.appState.$moveToHome) { moveToDashboard in
                    if moveToDashboard {
                        self.isView1Active = false
                        self.appState.moveToHome = false
                    }
                }
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
        .accentColor(Color(UIColor.onSurface))
    }
}
