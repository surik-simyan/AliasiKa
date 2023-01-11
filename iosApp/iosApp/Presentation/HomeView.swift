import SwiftUI
import MultiPlatformLibrary

struct HomeView: View {
    var body: some View {
        ZStack {
            Color(UIColor.secondary)
                .ignoresSafeArea()
            
            VStack {
                NavigationLink(destination: GamemodeView()) {
                    Text(SharedStrings.shared.play.localized())
                        .font(.system(size: 48, weight: .regular))
                        .foregroundColor(Color(UIColor.onSurface))
                        .frame(minWidth: 0, maxWidth: .infinity)
                        .padding(.vertical, CGFloat.dp(value: 24))
                    
                }
                .frame(minWidth: 0, maxWidth: .infinity)
                .background(Color(UIColor.surface))
                .cornerRadius(CGFloat.dp(value: 4))
                .padding()
            }
        }
    }
}
