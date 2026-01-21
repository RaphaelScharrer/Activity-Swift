package at.ac.htlleonding.dto;

import at.ac.htlleonding.model.Team;

public record TeamDTO(
    Long id,
    Long position
) {
    public static TeamDTO from(Team team) {
        return new TeamDTO(team.id, team.position);
    }

    public Team toEntity() {
        Team team = new Team();
        team.id = this.id;
        team.position = this.position;
        return team;
    }
}
