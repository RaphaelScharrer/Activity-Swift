import SwiftUI

struct TeamSelectView: View {
    @State private var playerName: String = ""
    @State private var players: [Player] = []
    
    // Team Arrays
    @State private var redTeam: [Player] = []
    @State private var blueTeam: [Player] = []
    @State private var greenTeam: [Player] = []
    @State private var yellowTeam: [Player] = []
    
    var body: some View {
        VStack(spacing: 20) {
            HStack {
                TextField("Spielername", text: $playerName)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .padding(.horizontal)
                
                Button("Hinzufügen") {
                    guard !playerName.isEmpty else { return }
                    let newPlayer = Player(name: playerName)
                    players.append(newPlayer)
                    playerName = ""
                }
                .padding(.trailing)
            }
            
            List {
                ForEach(players.indices, id: \.self) { index in
                    Text(players[index].name)
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(players[index].team.color)
                        .cornerRadius(8)
                        .onTapGesture {
                            players[index].team = players[index].team.next
                            updateTeams()
                        }
                }
            }
        }
        .padding()
    }
    
    private func updateTeams() {
        redTeam = players.filter { $0.team == .red }
        blueTeam = players.filter { $0.team == .blue }
        greenTeam = players.filter { $0.team == .green }
        yellowTeam = players.filter { $0.team == .yellow }
    }
}

struct Player: Identifiable, Equatable {
    let id = UUID()
    var name: String
    var team: Team = .none
}

enum Team: CaseIterable {
    case none, red, blue, green, yellow
    
    var color: Color {
        switch self {
        case .none: return Color.gray.opacity(0.3)
        case .red: return .red
        case .blue: return .blue
        case .green: return .green
        case .yellow: return .yellow
        }
    }
    
    var next: Team {
        switch self {
        case .none: return .red
        case .red: return .blue
        case .blue: return .green
        case .green: return .yellow
        case .yellow: return .none
        }
    }
}
