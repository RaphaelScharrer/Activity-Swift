import SwiftUI

struct Team: Identifiable, Equatable, Hashable {
    let id = UUID()
    let name: String
    let color: Color
    var points: Int = 0
    var position: Int = 1
}
