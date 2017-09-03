package com.szityu.stackoverflow.teamsandplayersdbexample.web;

import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Player;
import com.szityu.stackoverflow.teamsandplayersdbexample.repository.PlayerRepository;
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
@RequestMapping("/players")
public class PlayerController {
    private final PlayerRepository playerRepo;

    @Autowired
    public PlayerController(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }

    @GetMapping
    public Set<Player> getPlayers() {
        return newHashSet(playerRepo.findAll());
    }

    @GetMapping("/{id}")
    public Player getPlayer(@PathVariable("id") long id) {
        return ofNullable(playerRepo.findOne(id)).orElseThrow(create404Exception(id));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Player savePlayer(@RequestBody Player player) {
        return playerRepo.save(player);
    }

    @DeleteMapping("/{playerId}")
    public void deletePlayer(@PathVariable("playerId") Long playerId){
        try {
            playerRepo.delete(playerId);
        } catch (EmptyResultDataAccessException ignored) {
            throw create404Exception(playerId).get();
        }
    }

    private Supplier<NoSuchElementException> create404Exception(Long playerId) {
        return () -> new NoSuchElementException("There is no player with id #" + playerId);
    }
}
