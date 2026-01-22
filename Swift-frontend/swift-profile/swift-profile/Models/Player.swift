
import SwiftUI

struct Player: Identifiable {
    let id = UUID()
    var name: String
    var color: Color
    var position: Int = 0
}
