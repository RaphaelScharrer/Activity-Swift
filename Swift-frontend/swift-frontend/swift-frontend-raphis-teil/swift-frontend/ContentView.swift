import SwiftUI

struct TeamSelectView: View {
    @State private var playerName: String = ""
    @State private var players: [Player] = []

    @State private var teamGreen: [Player] = []
    @State private var teamBlue: [Player] = []
    @State private var teamYellow: [Player] = []
    @State private var teamRed: [Player] = []

    @State private var nextTeamIndex: Int = 0
    private let teamCycle: [Team] = [.green, .blue, .yellow, .red]

    var body: some View {
        NavigationView {
            VStack {
                HStack {
                    TextField("Spielername", text: $playerName)
                        .textFieldStyle(RoundedBorderTextFieldStyle())

                    Button("Hinzufügen") {
                        guard !playerName.trimmingCharacters(in: .whitespaces).isEmpty else { return }

                        let assignedTeam = teamCycle[nextTeamIndex % teamCycle.count]
                        nextTeamIndex += 1

                        let newPlayer = Player(name: playerName, team: assignedTeam)
                        players.append(newPlayer)
                        playerName = ""

                        updateTeams()
                    }
                }
                .padding()

                List {
                    ForEach(players.indices, id: \.self) { index in
                        let player = players[index]
                        Text(player.name)
                            .padding()
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(player.team.color.opacity(0.3))
                            .cornerRadius(8)
                            .onTapGesture {
                                players[index].team = players[index].team.next()
                                updateTeams()
                            }
                    }
                }

                Spacer()

                NavigationLink(destination: NextView()) {
                    Text("Fertig")
                        .foregroundColor(.white)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.blue)
                        .cornerRadius(12)
                        .padding(.horizontal, 50)
                }
                .padding(.bottom)
            }
            .navigationTitle("Spieler hinzufügen")
        }
    }

    func updateTeams() {
        teamGreen = players.filter { $0.team == .green }
        teamBlue = players.filter { $0.team == .blue }
        teamYellow = players.filter { $0.team == .yellow }
        teamRed = players.filter { $0.team == .red }
    }
}

struct Player: Identifiable, Equatable {
    let id = UUID()
    var name: String
    var team: Team
}

enum Team: CaseIterable {
    case green, blue, yellow, red

    func next() -> Team {
        switch self {
        case .green: return .blue
        case .blue: return .yellow
        case .yellow: return .red
        case .red: return .green
        }
    }

    var color: Color {
        switch self {
        case .green: return .green
        case .blue: return .blue
        case .yellow: return .yellow
        case .red: return .red
        }
    }
}
