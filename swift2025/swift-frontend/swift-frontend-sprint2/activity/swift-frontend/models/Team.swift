import SwiftUI

enum Team: String, CaseIterable, Identifiable {
    var id: String { self.rawValue }
    case red, green, blue, yellow

    var color: Color {
        switch self {
        case .red: return .red
        case .green: return .green
        case .blue: return .blue
        case .yellow: return .yellow
        }
    }

    func next() -> Team {
        let all = Team.allCases
        let idx = all.firstIndex(of: self)!
        return all[(idx + 1) % all.count]
    }
}
