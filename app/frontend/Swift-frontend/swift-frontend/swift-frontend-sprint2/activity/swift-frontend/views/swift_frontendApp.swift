import SwiftUI

@main
struct swift_frontendApp: App {
    @StateObject private var gameData = GameData()

    var body: some Scene {
        WindowGroup {
            TeamSelectView()
                .environmentObject(gameData)
        }
    }
}
