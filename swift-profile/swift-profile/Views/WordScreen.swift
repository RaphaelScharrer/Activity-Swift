
import SwiftUI

struct WordScreen: View {
    @ObservedObject var gameState: GameState
    @State private var revealWord = false
    @State private var timeRemaining = 60
    @State private var timerRunning = false
    @Environment(\.dismiss) var dismiss

    var body: some View {
        VStack(spacing: 20) {
            if !revealWord {
                Button("Begriff ansehen") {
                    revealWord = true
                }
            }

            if revealWord {
                Text("🔐 Begriff: Apfelstrudel")
                    .font(.title)
            }

            Button("Start") {
                startTimer()
            }

            Text("⏳ \(timeRemaining)s")
                .font(.largeTitle)

            Button("Erraten") {
                gameState.moveCurrentPlayer()
                dismiss()
            }
            .disabled(!timerRunning)
        }
        .padding()
    }

    func startTimer() {
        timerRunning = true
        Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { timer in
            if timeRemaining > 0 {
                timeRemaining -= 1
            } else {
                timer.invalidate()
                timerRunning = false
            }
        }
    }
}
