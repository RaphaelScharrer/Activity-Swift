import SwiftUI
import Combine

class GameData: ObservableObject {
    @Published var activeTeams: [Team] = [
        Team(name: "Blau", color: .blue),
        Team(name: "Grün", color: .green),
        Team(name: "Rot", color: .red),
        Team(name: "Gelb", color: .yellow)
    ]

    @Published var players: [Player] = []
    @Published var currentTeam: Team? = nil
    @Published var currentPlayerName: String = "-"
    @Published var winningTeam: Team? = nil  // hält das Gewinnerteam


    private var teamTurnQueue: [UUID] = []

    func prepareTeamTurnOrder() {
        teamTurnQueue = activeTeams.map { $0.id }
    }

    func nextTeamTurn() -> Team? {
        guard !teamTurnQueue.isEmpty else {
            prepareTeamTurnOrder()
            guard !teamTurnQueue.isEmpty else { return nil }
            return nextTeamTurn()
        }

        let nextTeamID = teamTurnQueue.removeFirst()
        teamTurnQueue.append(nextTeamID)

        return activeTeams.first(where: { $0.id == nextTeamID })
    }

    func getNextTeam() {
        if let nextTeam = nextTeamTurn() {
            currentTeam = nextTeam
            let teamPlayers = players.filter { $0.team.id == nextTeam.id }
            currentPlayerName = teamPlayers.randomElement()?.name ?? "?"
            print("Nächstes Team: \(nextTeam.name)")
        } else {
            currentTeam = nil
            currentPlayerName = "-"
            print("Kein nächstes Team vorhanden")
        }
    }

    func increasePointsAndPosition(for team: Team, by points: Int) {
        if let index = activeTeams.firstIndex(where: { $0.id == team.id }) {
            print("aktuelle points \(points)")
            print("aktuelle position \(activeTeams[index].position)")
            activeTeams[index].position += points
            print(" points \(points)")
            print("neue position \(activeTeams[index].position)")
            
            if(activeTeams[index].position >= 24) {
                teamWon(activeTeams[index])
            }
        }
    }
    
    func teamWon(_ team: Team) {
            winningTeam = team
            print("Team \(team.name) hat gewonnen")
        }
    

    // MARK: Spielerverwaltung

    func addPlayer(name: String) {
        guard !name.trimmingCharacters(in: .whitespaces).isEmpty else { return }

        if activeTeams.isEmpty {
            activeTeams = [
                Team(name: "Blau", color: .blue),
                Team(name: "Grün", color: .green),
                Team(name: "Rot", color: .red),
                Team(name: "Gelb", color: .yellow)
            ]
        }

        let teamIndex = players.count % activeTeams.count
        let team = activeTeams[teamIndex]

        let player = Player(name: name, team: team)
        players.append(player)
    }

    func removePlayer(_ player: Player) {
        players.removeAll { $0.id == player.id }
        let teamID = player.team.id
        let stillUsed = players.contains { $0.team.id == teamID }
        if !stillUsed {
            activeTeams.removeAll { $0.id == teamID }
        }
    }

    func changeTeam(for player: Player) {
        guard let currentIndex = activeTeams.firstIndex(where: { $0.id == player.team.id }) else { return }
        let nextIndex = (currentIndex + 1) % activeTeams.count
        let nextTeam = activeTeams[nextIndex]

        if let playerIndex = players.firstIndex(where: { $0.id == player.id }) {
            players[playerIndex].team = nextTeam
        }
    }

    func updateActiveTeamsBasedOnPlayers() {
        let usedTeamIDs = Set(players.map { $0.team.id })
        activeTeams = activeTeams.filter { usedTeamIDs.contains($0.id) }
    }

    func assignPlayersToRandomTeams() {
        activeTeams = [
            Team(name: "Blau", color: .blue),
            Team(name: "Grün", color: .green),
            Team(name: "Rot", color: .red),
            Team(name: "Gelb", color: .yellow)
        ]

        let shuffledPlayers = players.shuffled()
        for (index, player) in shuffledPlayers.enumerated() {
            let team = activeTeams[index % activeTeams.count]
            if let i = players.firstIndex(where: { $0.id == player.id }) {
                players[i].team = team
            }
        }

        prepareTeamTurnOrder()
    }

    var isValid: Bool {
        let teams = Dictionary(grouping: players, by: { $0.team.id })
        return teams.count >= 2 && players.count >= 2
    }
}
