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
    func stacked(at position: Int, in total: Int) -> some View {
        let offset = CGFloat(total - position)
        return self.offset(CGSize(width: offset * 20, height: offset * 20))
    }
}
