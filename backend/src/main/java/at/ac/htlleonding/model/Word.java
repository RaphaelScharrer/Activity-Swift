package at.ac.htlleonding.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Word extends PanacheEntity {

    @NotBlank
    @Column(nullable = false)
    public String word;

    public String definition;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    public Integer points;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public WordCategory category;

    public enum WordCategory {
        DRAW,
        ACT,
        DESCRIBE
    }

    public static java.util.stream.Stream<Word> streamAll() {
        return Word.<Word>listAll().stream();
    }

    public static java.util.stream.Stream<Word> streamByMinPoints(Integer minPoints) {
        return Word.<Word>list("points >= ?1", minPoints).stream();
    }

    public static Word findByWord(String word) {
        return find("word", word).firstResult();
    }

    public static Word findRandomWord() {
        long count = count();
        if (count == 0) {
            return null;
        }
        int randomIndex = (int) (Math.random() * count);
        return Word.<Word>findAll().page(randomIndex, 1).firstResult();
    }

    public static Word findRandomWordByCategory(WordCategory category) {
        java.util.List<Word> words = Word.<Word>list("category", category);
        if (words.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * words.size());
        return words.get(randomIndex);
    }

    public static long countWords() {
        return count();
    }

    public static void deleteAllWords() {
        delete("1=1");
    }
}