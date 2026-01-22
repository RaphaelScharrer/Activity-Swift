
import SwiftUI

@main
struct swift_profileApp: App {
    @StateObject var gameState = GameState()
    
    var body: some Scene {
        WindowGroup {
            if gameState.isGameStarted {
                GameBoardView(gameState: gameState)
            }
            else {
                PlayerSetupView(gameState: gameState)
            }
        }
    }
}
