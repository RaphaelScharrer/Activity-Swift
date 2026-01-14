
import SwiftUI

struct GameBoardView: View {
    @ObservedObject var gameState: GameState

    var body: some View {
        VStack {
            Grid(horizontalSpacing: 2, verticalSpacing: 2) {
                ForEach(0..<12) { row in
                    GridRow {
                        ForEach(0..<5) { col in
                            ZStack {
                                Rectangle()
                                    .fill(randomFieldColor(row: row, col: col))
                                    .frame(width: 60, height: 60)
                                ForEach(gameState.players.indices, id: \.self) { index in
                                    if gameState.players[index].position == row * 5 + col {
                                        Circle()
                                            .fill(gameState.players[index].color)
                                            .frame(width: 30, height: 30)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button("Zug machen") {
                gameState.showingWordScreen = true
            }
        }
        .sheet(isPresented: $gameState.showingWordScreen) {
            WordScreen(gameState: gameState)
        }
    }

    func randomFieldColor(row: Int, col: Int) -> Color {
        let colors: [Color] = [.blue, .green, .yellow, .orange, .pink]
        return colors[(row * 5 + col) % colors.count]
    }
}
