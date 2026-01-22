struct WonView: View {
    let team: Team
    let players: [Player]

    var body: some View {
        VStack(spacing: 20) {
            Text("Gewonnen hat:")
                .font(.headline)

            Text(team.name)
                .font(.largeTitle)
                .bold()
                .foregroundColor(team.color)

            Text("Spieler im Team:")
                .font(.title2)
                .padding(.top)

            List(players) { player in
                Text(player.name)
            }

            Spacer()
        }
        .padding()
        .navigationTitle("Gewinner")
        .navigationBarTitleDisplayMode(.inline)
    }
}
