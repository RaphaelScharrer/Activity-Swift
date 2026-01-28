package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Game;

import java.time.LocalDateTime;
import java.util.List;

public record GameDTO(
        Long id,
        String name,
        LocalDateTime createdOn,
        List<Long> teamIds
) {
    // Constructor from Game entity
    public GameDTO(Game game) {
        this(
                game.id,
                game.name,
                game.createdOn,
                game.teams != null
                        ? game.teams.stream().map(team -> team.id).toList()
                        : null
        );
    }

    // Convert back to entity
    public Game toEntity() {
        Game game = new Game();
        game.id = this.id;
        game.name = this.name;
        game.createdOn = this.createdOn;
        return game;
    }
}