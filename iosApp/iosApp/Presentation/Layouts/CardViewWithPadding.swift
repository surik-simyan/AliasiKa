import SwiftUI

struct CardViewWithPadding<Content: View>: View {
    var content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        ZStack(alignment: .top) {
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(UIColor.surface))
            content
                .padding()
        }
    }
}
