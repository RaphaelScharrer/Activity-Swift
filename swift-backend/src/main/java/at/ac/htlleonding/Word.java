package at.ac.htlleonding;

import jakarta.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name=Word.QUERY_FIND_BY_ID, query="select w from Word w where id = :id"),
        @NamedQuery(name=Word.QUERY_COUNT_ALL, query="select count(w) from Word w"),
        @NamedQuery(name=Word.QUERY_FIND_ALL, query = "select w from Word w")
        })
public class Word {

    public static final String QUERY_FIND_BY_ID = "Word.findById";
    public static final String QUERY_COUNT_ALL = "Word.countAll";
    public static final String QUERY_FIND_ALL = "Word.findAll";

    @Id
    @GeneratedValue
    private Long id;

    private String word;

    private String definition;

    private Integer points;

    public Word(Long id, String word, String definition, Integer points) {
        this.id = id;
        this.word = word;
        this.definition = definition;
        this.points = points;
    }

    public Word() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
