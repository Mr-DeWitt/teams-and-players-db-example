package com.szityu.stackoverflow.teamsandplayersdbexample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Player;
import com.szityu.stackoverflow.teamsandplayersdbexample.domain.Team;
import com.szityu.stackoverflow.teamsandplayersdbexample.repository.PlayerRepository;
import com.szityu.stackoverflow.teamsandplayersdbexample.repository.TeamRepository;
import com.szityu.stackoverflow.teamsandplayersdbexample.web.PlayerController;
import com.szityu.stackoverflow.teamsandplayersdbexample.web.TeamController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {PlayerController.class, TeamController.class})
public class TestMvc {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerRepository playerRepo;
    @MockBean
    private TeamRepository teamRepo;

    private Player TONY;
    private Player PETER;
    private Team AVENGERS;

    @Before
    public void setUp() throws Exception {

        TONY = new Player(42, "Tony");
        PETER = new Player(17, "Peter");

        AVENGERS = new Team("Avengers");
        AVENGERS.addPlayers(TONY, PETER);
    }

    @Test
    public void testSavePlayer() throws Exception {
        when(playerRepo.save(eq(TONY))).thenReturn(TONY);

        final String response = savePlayer(TONY);

        final Player savedPlayer = objectMapper.readValue(response, Player.class);

        assertThat(savedPlayer)
                .isEqualTo(TONY);
    }

    @Test
    public void testDeletePlayer() throws Exception {
        mockMvc.perform(delete("/players/{playerId}", 7L))
                .andExpect(status().isOk());

        verify(playerRepo, times(1)).delete(7L);
        verifyNoMoreInteractions(playerRepo);
    }

    @Test
    public void testGetPlayers() throws Exception {
        when(playerRepo.findAll()).thenReturn(newHashSet(TONY, PETER));

        final String result = mockMvc.perform(get("/players"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Player[] players = objectMapper.readValue(result, Player[].class);

        assertThat(players).containsExactlyInAnyOrder(TONY, PETER);
    }

    @Test
    public void testGetPlayer() throws Exception {
        when(playerRepo.findOne(eq(3L))).thenReturn(TONY);

        final String response = mockMvc.perform(get("/players/{id}", 3L))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Player player = objectMapper.readValue(response, Player.class);

        assertThat(player).isEqualTo(TONY);
    }

    @Test
    public void testGetTeams() throws Exception {
        when(teamRepo.findAll()).thenReturn(singleton(AVENGERS));

        final String response = mockMvc.perform(get("/teams"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Team[] teams = objectMapper.readValue(response, Team[].class);

        assertThat(teams).containsExactlyInAnyOrder(AVENGERS);
    }

    @Test
    public void testSaveTeam() throws Exception {
        when(teamRepo.save(eq(AVENGERS))).thenReturn(AVENGERS);

        final String response = mockMvc.perform(post("/teams").content(objectMapper.writeValueAsBytes(AVENGERS))
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        final Team team = objectMapper.readValue(response, Team.class);

        assertThat(team).isEqualTo(AVENGERS);
    }

    @Test
    public void testDeleteTeam() throws Exception {
        mockMvc.perform(delete("/teams/{teamId}", 6L))
                .andExpect(status().isOk());

        verify(teamRepo, times(1)).delete(6L);
        verifyNoMoreInteractions(teamRepo);
    }

    @Test
    public void testGetTeam() throws Exception {
        when(teamRepo.findOne(eq(4L))).thenReturn(AVENGERS);

        final Team team = getTeam(4L);

        assertThat(team).isEqualTo(AVENGERS);
    }

    @Test
    public void testGetPlayersOfTeam() throws Exception {
        when(teamRepo.findOne(5L)).thenReturn(AVENGERS);

        final String response = mockMvc.perform(get("/teams/{teamId}/players", 5L))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Player[] players = objectMapper.readValue(response, Player[].class);

        assertThat(newHashSet(players)).isEqualTo(AVENGERS.getPlayers());
    }

    @Test
    public void testAddPlayerToTeam() throws Exception {
        AVENGERS = new Team("Avengers");
        when(playerRepo.findOne(2L)).thenReturn(TONY);
        when(teamRepo.findOne(1L)).thenReturn(AVENGERS);

        assertThat(getTeam(1L).getPlayers()).isEmpty();

        mockMvc.perform(put("/teams/{teamId}/players/{playerId}", 1L, 2L))
                .andExpect(status().isOk());

        assertThat(getTeam(1L).getPlayers()).containsExactly(TONY);
    }

    @Test
    public void testRemovePlayerFromTeam() throws Exception {
        setField(TONY, "id", 2L);
        when(teamRepo.findOne(1L)).thenReturn(AVENGERS);

        assertThat(AVENGERS.getPlayers()).containsExactlyInAnyOrder(TONY, PETER);

        mockMvc.perform(delete("/teams/{teamId}/players/{playerId}", 1L, 2L))
                .andExpect(status().isOk());

        assertThat(getTeam(1L).getPlayers()).containsExactly(PETER);
    }

    private String savePlayer(Player player) throws Exception {
        return mockMvc.perform(post("/players")
                .content(objectMapper.writeValueAsString(player))
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    private Team getTeam(long id) throws Exception {
        final String response = mockMvc.perform(get("/teams/{id}", id))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(response, Team.class);
    }
}
