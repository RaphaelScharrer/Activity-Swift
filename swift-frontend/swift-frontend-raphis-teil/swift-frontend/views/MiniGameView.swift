import SwiftUI

struct MinigameView: View {
    @StateObject private var wordVM = WordViewModel()
    @EnvironmentObject var gameData: GameData

    @State private var showWord = false
    @State private var timerRunning = false
    @State private var timeRemaining = 65
    @State private var navigateToWinner = false

    @Environment(\.presentationMode) var presentationMode
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

    var body: some View {
        VStack(spacing: 24) {
            if showWord {
                VStack(spacing: 12) {
                    Text(wordVM.wordText)
                        .font(.largeTitle)
                        .bold()

                    Text(wordVM.definitionText)
                        .font(.body)
                        .multilineTextAlignment(.center)

                    Text("Punkte: \(wordVM.wordPoints)")
                        .font(.title3)
                        .foregroundColor(.secondary)

                    Text("Verbleibende Zeit: \(timeString(from: timeRemaining))")
                        .font(.title2)
                        .bold()
                        .foregroundColor(.red)
                        .onReceive(timer) { _ in
                            if timerRunning && timeRemaining > 0 {
                                timeRemaining -= 1
                            } else if timerRunning && timeRemaining == 0 {
                                timerRunning = false
                                // Zeit abgelaufen, zurück zur BoardView
                                presentationMode.wrappedValue.dismiss()
                            }
                        }

                    Button("Geschafft") {
                        if let team = gameData.currentTeam {
                            gameData.increasePointsAndPosition(for: team, by: wordVM.wordPoints)
                        }

                        timerRunning = false
                        showWord = false

                        if let winner = gameData.activeTeams.first(where: { $0.position >= 24 }) {
                            print("Team \(winner.name) hat gewonnen!")
                            gameData.winningTeam = winner
                            navigateToWinner = true
                        } else {
                            presentationMode.wrappedValue.dismiss()
                        }
                    }
                    .padding()
                    .background(Color.green)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                }
                .padding()
            } else {
                VStack(spacing: 12) {
                    if let team = gameData.currentTeam {
                        (
                            Text(gameData.currentPlayerName)
                                .foregroundColor(team.color)
                                .font(.title2)
                                .bold()
                            +
                            Text(" (\(team.name)), bist du bereit?")
                                .font(.title2)
                                .foregroundColor(.primary)
                        )
                    }

                    Button {
                        wordVM.fetchWord()
                        showWord = true
                        timeRemaining = 10 //65 eigentliche Zeit
                        timerRunning = true
                    } label: {
                        Text("Bereit!")
                            .font(.title2)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                }
                .padding(.horizontal)
            }

            Spacer()
        }
        .padding()
        .navigationTitle("Activity")

        // NavigationLink zur Gewinner-View
        NavigationLink(
            destination: Group {
                if let winningTeam = gameData.winningTeam {
                    WonView(
                        team: winningTeam,
                        players: gameData.players.filter { $0.team.id == winningTeam.id }
                    )
                } else {
                    EmptyView()
                }
            },
            isActive: $navigateToWinner
        ) {
            EmptyView()
        }
    }

    private func timeString(from seconds: Int) -> String {
        let minutes = seconds / 60
        let secs = seconds % 60
        return String(format: "%01d:%02d", minutes, secs)
    }
}
