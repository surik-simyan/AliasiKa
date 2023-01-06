import SwiftUI

struct CardView<Content: View>: View {
    var content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        ZStack(alignment: .top) {
            Color(UIColor.surface)
            content
        }
        .clipShape(RoundedRectangle(cornerRadius: 8))
    }
}
