
import SwiftUI

struct PlayerSetupView: View {
    @ObservedObject var gameState: GameState
    @State private var name = ""
    @State private var selectedColor: Color = .red
    @State private var showTeamPopup = false
    @State private var selectedTeamCount = 2

    let teamColors: [Color] = [.red, .blue, .green, .yellow]

    var body: some View {
        VStack {
            List {
                ForEach(gameState.players) { player in
                    HStack {
                        Circle().fill(player.color).frame(width: 20, height: 20)
                        Text(player.name)
                    }
                }
            }

            HStack {
                TextField("Name", text: $name)
                    .textFieldStyle(RoundedBorderTextFieldStyle())

                Picker("Farbe", selection: $selectedColor) {
                    ForEach(teamColors, id: \.self) { color in
                        Circle().fill(color).frame(width: 20, height: 20)
                    }
                }
                .pickerStyle(MenuPickerStyle())

                Button("Hinzufügen") {
                    guard !name.isEmpty else { return }
                    gameState.players.append(Player(name: name, color: selectedColor))
                    name = ""
                }
            }.padding()

            HStack {
                Button("Zufällige Teams") {
                    showTeamPopup = true
                }
                .padding()
                .confirmationDialog("Wie viele Teams?", isPresented: $showTeamPopup) {
                    ForEach(2...4, id: \.self) { count in
                        Button("\(count) Teams") {
                            gameState.numberOfTeams = count
                            gameState.assignRandomTeams()
                        }
                    }
                }

                Button("Start") {
                    gameState.isGameStarted = true
                }
                .padding()
            }
        }
    }
}
