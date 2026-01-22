package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Game;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GameDTO {

    public Long id;
    public String name;
    public LocalDateTime createdOn;
    public List<Long> teamIds;

    public GameDTO() {
    }

    public GameDTO(Game game) {
        this.id = game.id;
        this.name = game.name;
        this.createdOn = game.createdOn;
        if (game.teams != null) {
            this.teamIds = game.teams.stream()
                    .map(team -> team.id)
                    .collect(Collectors.toList());
        }
    }

    public Game toEntity() {
        Game game = new Game();
        game.id = this.id;
        game.name = this.name;
        game.createdOn = this.createdOn;
        return game;
    }
}
