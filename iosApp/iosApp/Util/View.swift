import Foundation
import SwiftUI

extension View {
    @ViewBuilder func scrollEnabled(_ enabled: Bool) -> some View {
        if enabled {
            self
        } else {
            simultaneousGesture(DragGesture(minimumDistance: 0),
                                including: .all)
        }
    }
}
