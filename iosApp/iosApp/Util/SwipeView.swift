import SwiftUI

struct SwipeView: View {
    let word: String
    var wordSwipe: (Bool) -> Void
    
    @State private var offset = CGSize.zero

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 8)
                .background(Color(UIColor.surface))
            VStack {
                Text(word)
                    .font(.largeTitle)
                    .foregroundColor(.black)
            }
            .padding()
            .multilineTextAlignment(.center)
        }
        .frame(width: 450, height: 250)
        .rotationEffect(.degrees(Double(offset.width / 5)))
        .offset(x: offset.width * 5, y: 0)
        .opacity(2 - Double(abs(offset.width / 50)))
        .accessibilityAddTraits(.isButton)
        .gesture(
            DragGesture()
                .onChanged { gesture in
                    offset = gesture.translation
                }
                .onEnded { _ in
                    if abs(offset.width) > 100 {
                        if offset.width > 0 {
                            wordSwipe(true)
                        } else {
                            wordSwipe(false)
                        }
                    } else {
                        offset = .zero
                    }
                }
        )
        .animation(.spring(), value: offset)
    }
}
