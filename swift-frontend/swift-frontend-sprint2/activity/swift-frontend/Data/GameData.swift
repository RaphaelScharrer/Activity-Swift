import SwiftUI

class GameData: ObservableObject {
    @Published var players: [Player] = []

    var activeTeams: [Team] {
        Array(Set(players.map { $0.team }))
    }

    var isValid: Bool {
        players.count >= 4
    }
    
    var validPlayersPerTeam: Bool {
        let teamCounts = Dictionary(grouping: players, by: { $0.team }).mapValues { $0.count }
        return teamCounts.values.filter { $0 >= 2 }.count >= 2
    }

    func addPlayer(name: String) {
        guard !name.isEmpty else { return }
        let defaultTeam = Team.allCases.first ?? .red
        players.append(Player(name: name, team: defaultTeam))
    }

    func removePlayer(_ player: Player) {
        players.removeAll { $0 == player }
    }

    func changeTeam(for player: Player) {
        guard let index = players.firstIndex(of: player) else { return }
        let currentTeam = players[index].team
        let allTeams = Team.allCases
        if let currentIdx = allTeams.firstIndex(of: currentTeam) {
            let nextTeam = allTeams[(currentIdx + 1) % allTeams.count]
            players[index].team = nextTeam
        }
    }

    func assignPlayersToRandomTeams() {
        guard players.count >= 4 else { return }

        let teamCount = max(2, players.count / 2)
        let teams = Array(Team.allCases.prefix(teamCount))

        var teamBuckets = Array(repeating: [Player](), count: teamCount)
        var shuffled = players.shuffled()

        for i in 0..<shuffled.count {
            let teamIndex = i % teamCount
            shuffled[i].team = teams[teamIndex]
            teamBuckets[teamIndex].append(shuffled[i])
        }

        players = teamBuckets.flatMap { $0 }
    }

    // MARK: - Turnus-Logik für Spielerrotation

    private var teamOrder: [Team] = []
    private var teamPlayers: [Team: [Player]] = [:]
    private var currentPlayerIndices: [Team: Int] = [:]
    private var currentTeamTurnIndex = 0

    func prepareTurnOrder() {
        teamPlayers = Dictionary(grouping: players, by: { $0.team })
        teamOrder = teamPlayers.keys.sorted { $0.rawValue < $1.rawValue }
        currentPlayerIndices = Dictionary(uniqueKeysWithValues: teamOrder.map { ($0, 0) })
        currentTeamTurnIndex = 0
    }

    func nextPlayerTurn() -> (name: String, color: Color)? {
        guard !teamOrder.isEmpty else { return nil }

        var tries = 0
        while tries < teamOrder.count {
            let team = teamOrder[currentTeamTurnIndex]
            if let teamList = teamPlayers[team], !teamList.isEmpty {
                let index = currentPlayerIndices[team] ?? 0
                let player = teamList[index % teamList.count]
                currentPlayerIndices[team] = index + 1
                currentTeamTurnIndex = (currentTeamTurnIndex + 1) % teamOrder.count
                return (player.name, team.color)
            } else {
                currentTeamTurnIndex = (currentTeamTurnIndex + 1) % teamOrder.count
                tries += 1
            }
        }
        return nil
    }

}
