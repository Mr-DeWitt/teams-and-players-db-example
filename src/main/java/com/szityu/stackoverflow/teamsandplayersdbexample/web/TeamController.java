package com.szityu.stackoverflow.teamsandplayersdbexample.web;

import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Player;
import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Team;
import com.szityu.stackoverflow.teamsandplayersdbexample.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Transactional
@RequestMapping("/teams")
public class TeamController {

    private final TeamRepository teamRepo;
    private final PlayerController playerController;

    @Autowired
    public TeamController(TeamRepository teamRepo, PlayerController playerController) {
        this.teamRepo = teamRepo;
        this.playerController = playerController;
    }

    @GetMapping
    public Set<Team> getTeams() {
        return newHashSet(teamRepo.findAll());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Team saveTeam(@RequestBody Team team) {
        return teamRepo.save(team);
    }

    @DeleteMapping("/{teamId}")
    public void deleteTeam(@PathVariable Long teamId) {
        try {
            teamRepo.delete(teamId);
        } catch (EmptyResultDataAccessException ignored) {
            throw create404Exception(teamId).get();
        }
    }

    @GetMapping("/{id}")
    public Team getTeam(@PathVariable("id") Long id) {
        return ofNullable(teamRepo.findOne(id)).orElseThrow(create404Exception(id));
    }

    @GetMapping("/{teamId}/players")
    public Set<Player> getPlayersOfTeam(@PathVariable("teamId") Long teamId) {
        return getTeam(teamId).getPlayers();
    }

    @PutMapping("/{teamId}/players/{playerId}")
    public void addPlayerToTeam(@PathVariable("teamId") Long teamId, @PathVariable("playerId") Long playerId) {
        getTeam(teamId).addPlayer(playerController.getPlayer(playerId));
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public void removePlayerFromTeam(@PathVariable("teamId") Long teamId, @PathVariable("playerId") Long playerId) {
        getTeam(teamId).removePlayer(playerId);
    }

    private Supplier<NoSuchElementException> create404Exception(Long teamId) {
        return () -> new NoSuchElementException("There is no team with id #" + teamId);
    }
}
