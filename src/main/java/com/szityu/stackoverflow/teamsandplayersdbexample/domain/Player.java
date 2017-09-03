package com.szityu.stackoverflow.teamsandplayersdbexample.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"age", "name"}))
public class Player {

    @Getter
    @Column
    private final int age;
    @Getter
    @Column
    private final String name;
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    // For JPA.
    @SuppressWarnings("unused")
    protected Player() {
        age = 0;
        name = null;
    }

    @JsonCreator
    public Player(@JsonProperty("age") int age, @JsonProperty("name") String name) {
        this.age = age;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return age == player.age &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("age", age)
                .add("name", name)
                .toString();
    }
}
