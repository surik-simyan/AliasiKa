import Foundation
import SwiftUI

extension CGFloat {
    static func dp(value: CGFloat) -> CGFloat {
        return value * UIScreen.main.scale
    }
}
