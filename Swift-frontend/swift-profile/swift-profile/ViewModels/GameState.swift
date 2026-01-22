
import SwiftUI

class GameState: ObservableObject {
    @Published var players: [Player] = []
    @Published var numberOfTeams: Int = 2
    @Published var isGameStarted = false
    @Published var currentPlayerIndex = 0
    @Published var showingWordScreen = false

    func assignRandomTeams() {
        let teamColors: [Color] = [.red, .blue, .green, .yellow]
        let selectedColors = Array(teamColors.prefix(numberOfTeams))
        var shuffledPlayers = players.shuffled()
        for i in shuffledPlayers.indices {
            shuffledPlayers[i].color = selectedColors[i % numberOfTeams]
        }
        players = shuffledPlayers
    }

    func moveCurrentPlayer() {
        players[currentPlayerIndex].position += 1
        currentPlayerIndex = (currentPlayerIndex + 1) % players.count
    }
}
