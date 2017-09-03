package com.szityu.stackoverflow.teamsandplayersdbexample;

import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Player;
import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Team;
import com.szityu.stackoverflow.teamsandplayersdbexample.repository.PlayerRepository;
import com.szityu.stackoverflow.teamsandplayersdbexample.repository.TeamRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDatabase {
    @Autowired
    private PlayerRepository playerRepo;
    @Autowired
    private TeamRepository teamRepo;

    private Player TONY;
    private Player PETER;
    private Team AVENGERS;

    @Before
    public void setUp() throws Exception {
        cleanDb();

        TONY = new Player(42, "Tony");
        PETER = new Player(17, "Peter");

        AVENGERS = new Team("Avengers");
        AVENGERS.addPlayers(TONY, PETER);
    }

    private void cleanDb() {
        stream(teamRepo.findAll().spliterator(), false).forEach(teamRepo::delete);
        stream(playerRepo.findAll().spliterator(), false).forEach(playerRepo::delete);
    }

    @Test
    public void testPersistPlayer() throws Exception {
        assertThat(playerRepo.findAll()).isEmpty();

        TONY = playerRepo.save(TONY);
        PETER = playerRepo.save(PETER);

        assertThat(playerRepo.findAll()).containsExactly(TONY, PETER);
    }

    @Test
    public void testPersistTeam() throws Exception {
        testPersistPlayer();

        AVENGERS = teamRepo.save(AVENGERS);

        final Iterable<Team> savedTeam = teamRepo.findAll();
        assertThat(savedTeam).containsExactly(AVENGERS);
        assertThat(savedTeam.iterator().next().getPlayers()).containsExactly(TONY, PETER);
    }
}
