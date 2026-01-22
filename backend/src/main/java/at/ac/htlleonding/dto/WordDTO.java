package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Word;

public record WordDTO(
    Long id,
    String word,
    String definition,
    Integer points,
    Word.WordCategory category
) {
    public static WordDTO from(Word word) {
        return new WordDTO(
            word.id,
            word.word,
            word.definition,
            word.points,
                word.category
        );
    }

    public Word toEntity() {
        Word word = new Word();
        word.id = this.id;
        word.word = this.word;
        word.definition = this.definition;
        word.points = this.points;
        word.category = this.category;
        return word;
    }
}
