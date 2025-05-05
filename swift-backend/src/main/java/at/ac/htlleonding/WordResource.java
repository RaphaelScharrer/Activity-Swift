package at.ac.htlleonding;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/activity")
public class WordResource {

    @Inject
    WordRepository wordRepository;

    @GET
    @Path("/getRandomWord")
    @Produces(MediaType.APPLICATION_JSON)
    public Word getRandomWord() {
        return wordRepository.getRandomWord();
    }


}
