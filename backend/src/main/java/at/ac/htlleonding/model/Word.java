package at.ac.htlleonding.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.Random;
import java.util.stream.Stream;

@Entity
public class Word extends PanacheEntity {

    @NotBlank(message = "Wort darf nicht leer sein")
    @Column(nullable = false, unique = true)
    public String word;

    @NotBlank(message = "Definition darf nicht leer sein")
    @Column(nullable = false, length = 1000)
    public String definition;

    @Min(0)
    @Column(nullable = false)
    public Integer points;

    // Panache Query Methods mit Streams
    public static Stream<Word> streamAll() {
        return stream("ORDER BY word");
    }

    public static Word findByWord(String word) {
        return find("LOWER(word) = LOWER(?1)", word).firstResult();
    }

    public static Stream<Word> streamByMinPoints(Integer minPoints) {
        return stream("points >= ?1 ORDER BY points DESC", minPoints);
    }

    public static Word findRandomWord() {
        long count = count();
        if (count == 0) {
            return null;
        }
        int randomIndex = new Random().nextInt((int) count);
        return findAll().page(randomIndex, 1).firstResult();
    }

    public static void deleteAllWords() {
        delete("1=1");
    }
}