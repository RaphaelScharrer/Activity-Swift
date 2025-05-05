package at.ac.htlleonding;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class WordRepository {

    @Inject
    EntityManager em;


    public Word getRandomWord() {
        List<Word> words = em.createNamedQuery("Word.findAll", Word.class).getResultList();
        int count = words.size();
        int randomNum = ThreadLocalRandom.current().nextInt(0, count + 1);
        return words.get(randomNum);
    }
}
