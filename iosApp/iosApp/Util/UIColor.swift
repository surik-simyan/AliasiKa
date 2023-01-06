import Foundation
import SwiftUI
import MultiPlatformLibrary

extension UIColor {
    convenience init(hex: Int64, alpha: Double = 1) {
        self.init(
            red: CGFloat((hex >> 16) & 0xff) / 255,
            green: CGFloat((hex >> 08) & 0xff) / 255,
            blue: CGFloat((hex >> 00) & 0xff) / 255,
            alpha: CGFloat(alpha)
        )
    }
    
    static let primary = UIColor(hex: ColorsKt.Orange500)
    static let primaryVariant = UIColor(hex: ColorsKt.Orange800)
    static let secondary = UIColor { traitCollection in
        return traitCollection.userInterfaceStyle == .dark ? UIColor(hex:ColorsKt.Black) : UIColor(hex:ColorsKt.Orange700)
    }
    static let surface = UIColor { traitCollection in
        return traitCollection.userInterfaceStyle == .dark ? UIColor(hex:ColorsKt.BlackSurface) : UIColor.white
    }
    static let onSurface = UIColor { traitCollection in
        return traitCollection.userInterfaceStyle == .dark ? UIColor.white : UIColor.black
    }
}
