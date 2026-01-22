package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Team;

import java.util.List;
import java.util.stream.Collectors;

public class TeamDTO {

    public Long id;
    public Integer position;
    public Long gameId;
    public List<Long> playerIds;

    public TeamDTO() {
    }

    public TeamDTO(Team team) {
        this.id = team.id;
        this.position = team.position;
        if (team.game != null) {
            this.gameId = team.game.id;
        }
        if (team.players != null) {
            this.playerIds = team.players.stream()
                    .map(player -> player.id)
                    .collect(Collectors.toList());
        }
    }

    public Team toEntity() {
        Team team = new Team();
        team.id = this.id;
        team.position = this.position;
        return team;
    }
}