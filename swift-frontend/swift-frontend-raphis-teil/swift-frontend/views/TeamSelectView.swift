import SwiftUI

struct TeamSelectView: View {
    @EnvironmentObject var gameData: GameData
    @State private var playerName: String = ""
    @State private var navigateNext: Bool = false

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Blauer Header mit sicherem Top-Abstand
                VStack(spacing: 12) {
                    Spacer().frame(height: 40) // Abstand zur Kamera/Notch

                    Text("Spieler hinzufügen")
                        .font(.title2)
                        .bold()
                        .foregroundColor(.white)

                    HStack {
                        TextField("Spielername", text: $playerName)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .padding(6)
                            .background(Color.white)
                            .cornerRadius(6)

                        Button("Hinzufügen") {
                            gameData.addPlayer(name: playerName)
                            playerName = ""
                        }
                        .disabled(playerName.trimmingCharacters(in: .whitespaces).isEmpty)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.white)
                        .foregroundColor(playerName.trimmingCharacters(in: .whitespaces).isEmpty ? Color.gray : Color.blue)
                        .cornerRadius(6)
                    }
                    .padding(.horizontal)
                    .padding(.bottom, 8)
                }
                .background(Color.blue)
                .edgesIgnoringSafeArea(.top)
                .shadow(radius: 4)

                // Spielerliste
                List {
                    ForEach(gameData.players) { player in
                        HStack {
                            Text(player.name)
                                .padding(6)
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .background(player.team.color.opacity(0.3))
                                .cornerRadius(8)
                                .onTapGesture {
                                    gameData.changeTeam(for: player)
                                }

                            Button(action: {
                                gameData.removePlayer(player)
                            }) {
                                Image(systemName: "minus.circle")
                                    .foregroundColor(.red)
                            }
                            .buttonStyle(BorderlessButtonStyle())
                        }
                    }
                }
                .listStyle(PlainListStyle())

                Spacer()

                // Untere Buttons
                HStack(spacing: 20) {
                    NavigationLink(destination: TeamPlayersView(), isActive: $navigateNext) {
                        EmptyView()
                    }

                    Button("Fertig") {
                        navigateNext = true
                    }
                    .disabled(gameData.players.count < 2)
                    .foregroundColor(.white)
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(gameData.players.count >= 2 ? Color.blue : Color.gray)
                    .cornerRadius(12)

                    Button {
                        gameData.assignPlayersToRandomTeams()
                    } label: {
                        Image(systemName: "questionmark.circle")
                            .font(.title2)
                            .foregroundColor(.blue)
                            .padding(10)
                            .background(Color.white)
                            .clipShape(Circle())
                            .shadow(radius: 2)
                    }
                }
                .padding(.horizontal, 30)
                .padding(.bottom)
            }
            .background(Color(.systemGroupedBackground))
            .navigationBarHidden(true)
            .onAppear {
                if gameData.activeTeams.count < 2 {
                    gameData.assignPlayersToRandomTeams()
                }
            }
        }
    }
}
