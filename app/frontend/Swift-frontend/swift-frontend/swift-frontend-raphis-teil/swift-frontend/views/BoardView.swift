import SwiftUI

struct PlayerMarker: View {
    var color: Color

    var body: some View {
        Circle()
            .fill(color)
            .frame(width: 16, height: 16)
            .overlay(Circle().stroke(Color.white, lineWidth: 1.5))
            .shadow(radius: 1)
    }
}

struct FieldView: View {
    let number: Int
    let isStart: Bool
    let isGoal: Bool
    let teamColors: [Color]

    var body: some View {
        ZStack(alignment: .topTrailing) {
            RoundedRectangle(cornerRadius: 8)
                .fill(Color.blue.opacity(0.7))
                .frame(width: 70, height: 70)
                .shadow(radius: 3)

            VStack {
                if isStart {
                    Text("START")
                        .font(.caption2)
                        .bold()
                        .foregroundColor(.white)
                        .padding(4)
                        .background(Color.green)
                        .cornerRadius(4)
                        .padding(.top, 4)
                } else if isGoal {
                    Text("ZIEL")
                        .font(.caption2)
                        .bold()
                        .foregroundColor(.white)
                        .padding(4)
                        .background(Color.red)
                        .cornerRadius(4)
                        .padding(.top, 4)
                } else {
                    Text("\(number)")
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding(.top, 6)
                }
                Spacer()
            }

            VStack(spacing: 4) {
                ForEach(teamColors.indices, id: \.self) { idx in
                    PlayerMarker(color: teamColors[idx])
                }
            }
            .padding(6)
        }
    }
}

struct BoardView: View {
    @EnvironmentObject var gameData: GameData
    let rows = 6
    let columns = 4
    let totalFields = 24
    
    @State var navigateToMinigame = false
    @State private var hasLoaded = false
    

    var body: some View {
        VStack {
            NavigationLink(destination: MinigameView(), isActive: $navigateToMinigame) {
                EmptyView()
                
            }
            
        VStack(spacing: 12) {
            VStack(spacing: 12) {
                ForEach((0..<rows).reversed(), id: \.self) { row in
                    HStack(spacing: 12) {
                        ForEach(0..<columns, id: \.self) { col in
                            let number = fieldNumber(row: row, col: col)
                            FieldView(
                                number: number,
                                isStart: number == 1,
                                isGoal: number == totalFields,
                                teamColors: gameData.activeTeams
                                    .filter { $0.position == number }
                                    .map { $0.color }
                            )
                        }
                    }
                }
            }
            .padding(.horizontal)

            
            Spacer()

            Button(action: {
                        // Hier dein Code, der beim Drücken ausgeführt wird
                        //code abschnitt den ich nichtmehr brauche. Jaman perfekt.
                gameData.getNextTeam()
                        // Danach Navigation starten
                        navigateToMinigame = true
                    }) {
                        Text("Nächste Runde")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(width: 220, height: 50)
                            .background(Color.orange)
                            .cornerRadius(14)
                            .shadow(radius: 3)
                    }
                }
                .padding(.bottom)
            }
        
        .padding()
        .background(Color(UIColor.systemGroupedBackground).ignoresSafeArea())
        .navigationTitle("Activity")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarHidden(true)

    }

    private func fieldNumber(row: Int, col: Int) -> Int {
        row % 2 == 0
            ? row * columns + col + 1
            : row * columns + (columns - col)
    }
}


