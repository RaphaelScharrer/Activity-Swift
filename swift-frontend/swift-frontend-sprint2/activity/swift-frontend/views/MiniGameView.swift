import SwiftUI

struct MinigameView: View {
    @StateObject private var wordVM = WordViewModel()
    @EnvironmentObject var gameData: GameData

    @State private var showWord = false
    @State private var timerRunning = false
    @State private var timeRemaining = 65

    @State private var currentName: String = ""
    @State private var currentColor: Color = .gray

    @Environment(\.presentationMode) var presentationMode

    var timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()

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

                    Text("Verbleibende Zeit: \(timeString(from: timeRemaining))")
                        .font(.title2)
                        .bold()
                        .foregroundColor(.red)
                        .onReceive(timer) { _ in
                            if timerRunning && timeRemaining > 0 {
                                timeRemaining -= 1
                            } else {
                                timerRunning = false
                            }
                        }

                    Button("Geschafft") {
                        timerRunning = false
                        presentationMode.wrappedValue.dismiss()
                    }
                    .padding()
                    .background(Color.green)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                }
                .padding()
            } else {
                VStack(spacing: 12) {
                    // Text ohne "Team", Name in Teamfarbe
                    (
                        Text(currentName)
                            .foregroundColor(currentColor)
                            .font(.title2)
                            .bold()
                        +
                        Text(", bist du bereit?")
                            .font(.title2)
                            .foregroundColor(.primary)
                    )

                    Button(action: {
                        wordVM.fetchWord()
                        showWord = true
                        timeRemaining = 65
                        timerRunning = true
                    }) {
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
        .onAppear {
            gameData.prepareTurnOrder()
            getNextPlayer()
        }
        .onChange(of: showWord) { newValue in
            if !newValue {
                getNextPlayer()
            }
        }
    }

    private func getNextPlayer() {
        if let next = gameData.nextPlayerTurn() {
            currentName = next.name
            currentColor = next.color
        } else {
            currentName = "-"
            currentColor = .gray
        }
    }

    private func timeString(from seconds: Int) -> String {
        let minutes = seconds / 60
        let secs = seconds % 60
        return String(format: "%01d:%02d", minutes, secs)
    }
}
