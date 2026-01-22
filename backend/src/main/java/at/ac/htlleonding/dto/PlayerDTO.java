package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Player;
import at.ac.htlleonding.model.Team;

public record PlayerDTO(
    Long id,
    Long team,
    String name,
    Long pointsEarned
) {
    public static PlayerDTO from(Player player) {
        return new PlayerDTO(
            player.id,
            player.team != null ? player.team.id : null,
            player.name,
            player.pointsEarned
        );
    }

    public Player toEntity(Team team) {
        Player player = new Player();
        player.id = this.id;
        player.name = this.name;
        player.pointsEarned = this.pointsEarned != null ? this.pointsEarned : (Long) 0L;
        player.team = team;
        return player;
    }
}
