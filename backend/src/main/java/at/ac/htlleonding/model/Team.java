package at.ac.htlleonding.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Stream;

@Entity
public class Team extends PanacheEntity {

    @NotNull
    @Min(0)
    @Column(nullable = false)
    public Long position;

    // Beziehungen
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Player> players;

    // Panache Query Methods mit Streams
    public static Stream<Team> streamAll() {
        return stream("ORDER BY position");
    }

    public static Team findByPosition(Long position) {
        return find("position", position).firstResult();
    }

    public static long countTeams() {
        return count();
    }

    public static void deleteAllTeams() {
        delete("1=1");
    }
}
