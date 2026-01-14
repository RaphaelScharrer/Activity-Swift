import SwiftUI

struct Player: Identifiable, Equatable {
    let id: UUID
    var name: String
    var team: Team   // ← war vorher `let team`

    init(id: UUID = UUID(), name: String, team: Team) {
        self.id = id
        self.name = name
        self.team = team
    }
}
