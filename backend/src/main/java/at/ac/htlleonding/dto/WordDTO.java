package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Word;

public record WordDTO(
    Long id,
    String word,
    String definition,
    Integer points
) {
    public static WordDTO from(Word word) {
        return new WordDTO(
            word.id,
            word.word,
            word.definition,
            word.points
        );
    }

    public Word toEntity() {
        Word word = new Word();
        word.id = this.id;
        word.word = this.word;
        word.definition = this.definition;
        word.points = this.points;
        return word;
    }
}
