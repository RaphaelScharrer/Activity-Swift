package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Team;

import java.util.List;

public record TeamDTO(
        Long id,
        Integer position,
        Long gameId,
        List<Long> playerIds
) {
    public TeamDTO(Team team) {
        this(
                team.id,
                team.position,
                team.game != null ? team.game.id : null,
                team.players != null
                        ? team.players.stream().map(player -> player.id).toList()
                        : null
        );
    }

    public Team toEntity() {
        Team team = new Team();
        team.id = this.id;
        team.position = this.position;
        return team;
    }
}