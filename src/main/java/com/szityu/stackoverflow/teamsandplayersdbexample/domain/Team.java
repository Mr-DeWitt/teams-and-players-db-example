package com.szityu.stackoverflow.teamsandplayersdbexample.domain;

import com.google.common.base.MoreObjects;
import lombok.Getter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static javax.persistence.FetchType.EAGER;

@Entity
public class Team {

    @Getter
    @Column(unique = true)
    private final String name;
    @OneToMany(fetch = EAGER)
    private final Set<Player> players = new HashSet<>();
    @Id
    @GeneratedValue
    @Getter
    private Long id;


    // For JPA.
    @SuppressWarnings("unused")
    protected Team() {
        name = null;
    }

    public Team(String name) {
        this.name = name;
    }

    public void addPlayers(Player... players) {
        stream(players).forEach(this::addPlayer);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Long playerId) {
        players.removeIf(player -> Objects.equals(player.getId(), playerId));
    }

    public Set<Player> getPlayers() {
        return newHashSet(players);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}
