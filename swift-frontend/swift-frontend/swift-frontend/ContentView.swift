import SwiftUI

struct ContentView: View {
    @StateObject private var viewModel = WordViewModel()

    var body: some View {
        VStack(spacing: 20) {
            Text("Wort des Tages:")
                .font(.title2)
                .bold()

            if viewModel.isLoading {
                ProgressView()
            } else {
                VStack(spacing: 10) {
                    Text(viewModel.wordText)
                        .font(.largeTitle)
                        .foregroundColor(.blue)
                        .bold()
                    Text(viewModel.definitionText)
                        .font(.body)
                        .italic()
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)
                }
            }

            Button(action: {
                viewModel.fetchWord()
            }) {
                Text("Neues Wort laden")
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(12)
            }
        }
        .padding()
    }
}
