func fetchWord() {
    guard let url = URL(string: "http://localhost:8080/api/word/random") else {
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
            let decoded = try JSONDecoder().decode(Word.self, from: data)
            DispatchQueue.main.async {
                self?.word = decoded.word
            }
        } catch {
            print("JSON Fehler: \(error.localizedDescription)")
        }
    }.resume()
}
