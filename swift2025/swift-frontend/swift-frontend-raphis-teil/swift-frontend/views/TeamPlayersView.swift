import SwiftUI

struct TeamPlayersView: View {
    @EnvironmentObject var gameData: GameData
    @State private var navigateToBoard = false

    var body: some View {
        VStack {
            Text("Teams und Spieler")
                .font(.largeTitle)
                .bold()
                .padding()

            List {
                ForEach(gameData.activeTeams.sorted(by: { $0.name < $1.name })) { team in
                    Section(header:
                        HStack {
                            Circle()
                                .fill(team.color)
                                .frame(width: 20, height: 20)
                            Text(team.name)
                                .font(.headline)
                                .foregroundColor(team.color)
                        }
                    ) {
                        let playersForTeam = gameData.players.filter { $0.team.id == team.id }
                        if playersForTeam.isEmpty {
                            Text("Keine Spieler")
                                .foregroundColor(.secondary)
                        } else {
                            ForEach(playersForTeam) { player in
                                Text(player.name)
                            }
                        }
                    }
                }
            }
            .onAppear{
                gameData.updateActiveTeamsBasedOnPlayers()
                gameData.prepareTeamTurnOrder()

            }
            .listStyle(InsetGroupedListStyle())

            Spacer()

            NavigationLink(destination: BoardView().environmentObject(gameData), isActive: $navigateToBoard) {
                EmptyView()
            }

            Button(action: {
                navigateToBoard = true
            }) {
                Text("Weiter")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(12)
                    .padding()
            }
        }
        .navigationTitle("Team Übersicht")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarHidden(true)

    }
}
