import SwiftUI

struct Player: Identifiable, Equatable {
    let id = UUID()
    var name: String
    var team: Team
}
