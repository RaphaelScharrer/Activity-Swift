import Foundation

class WordViewModel: ObservableObject {
    @Published var wordText: String = ""
    @Published var definitionText: String = ""
    @Published var wordPoints: Int = 0
    @Published var isLoading: Bool = false

    func fetchWord() {
        guard let url = URL(string: "http://localhost:8080/activity/getRandomWord") else {
            print("Ungültige URL")
            return
        }

        isLoading = true

        URLSession.shared.dataTask(with: url) { [weak self] data, response, error in
            DispatchQueue.main.async {
                self?.isLoading = false
            }

            guard let data = data, error == nil else {
                print("Fehler beim Laden: \(error?.localizedDescription ?? "Unbekannter Fehler")")
                return
            }

            do {
                let decodedWord = try JSONDecoder().decode(Word.self, from: data)
                DispatchQueue.main.async {
                    self?.wordText = decodedWord.word
                    self?.definitionText = decodedWord.definition

                    // Beispiel: wordPoints zufällig zwischen 1 und 10 setzen
                    self?.wordPoints = decodedWord.points
                }
            } catch {
                print("JSON-Fehler: \(error.localizedDescription)")
            }
        }.resume()
    }
}
