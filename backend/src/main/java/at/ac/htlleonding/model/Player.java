package at.ac.htlleonding.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.stream.Stream;

@Entity
public class Player extends PanacheEntity {

    @NotBlank(message = "Name darf nicht leer sein")
    @Column(nullable = false, unique = true)
    public String name;

    @Min(0)
    @Column(nullable = false)
    public Long pointsEarned = 0L;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    public Team team;

    // Panache Query Methods mit Streams
    public static Stream<Player> streamAll() {
        return stream("ORDER BY name");
    }

    public static Stream<Player> streamByTeam(Team team) {
        return stream("team", team);
    }

    public static Player findByName(String name) {
        return find("LOWER(name) = LOWER(?1)", name).firstResult();
    }

    public static List<Player> findByTeamId(Long teamId) {
        return list("team.id", teamId);
    }

    public static long countByTeam(Team team) {
        return count("team", team);
    }

    public static void deleteAllPlayers() {
        delete("1=1");
    }
}
