package server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Message;
import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import server.object.exceptions.PlayerNotFoundException;
import server.services.GameManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MultiplayerHandler.class)
class MultiplayerHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GameManager manager;
    @MockBean
    SimpMessagingTemplate msg;

    List<Player> playerList;
    List<Message> messageLog;
    Player player1;
    Player player2;
    String player1InJSON;
    String player2InJSON;

    @BeforeEach
    void setUp() throws JsonProcessingException, PlayerNotFoundException {
        playerList = new ArrayList<>();
        messageLog = new ArrayList<>();
        player1 = new Player("nickname1");
        player2 = new Player("nickname2");

        var ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        player1InJSON = ow.writeValueAsString(player1);
        player2InJSON = ow.writeValueAsString(player2);

        doAnswer((InvocationOnMock invocation) -> {
            if (playerList.contains(invocation.getArgument(0)))
                throw new IllegalArgumentException();
            playerList.add(invocation.getArgument(0));
            return null;
        }).when(manager).addToLobby(isA(Player.class));
        doAnswer((InvocationOnMock invocation) -> {
            if (playerList.stream().noneMatch(p -> p.nickname.equals(invocation.getArgument(0)))) {
                throw new PlayerNotFoundException("Player not found");
            }
            playerList.removeIf(p -> p.nickname.equals(invocation.getArgument(0)));
            return null;
        }).when(manager).removeFromLobby(anyString());
        doReturn(playerList).when(manager).getPlayersInLobby();

        doAnswer((InvocationOnMock invocation) -> {
            messageLog.add(invocation.getArgument(1));
            return null;
        }).when(msg).convertAndSend(eq("/topic/lobby"), isA(Message.class));
    }

    @Test
    void joinLobby() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/game/lobby")
                        .content(player1InJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(player1, playerList.get(0));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/game/lobby")
                        .content(player2InJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals(List.of(player1, player2), playerList);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/game/lobby")
                        .content(player1InJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isImUsed());
        assertEquals("JOIN", messageLog.get(0).getType());
        assertEquals("JOIN", messageLog.get(1).getType());
        assertEquals(player1.nickname, messageLog.get(0).getContent());
        assertEquals(player2.nickname, messageLog.get(1).getContent());
    }

    @Test
    void leaveLobby() throws Exception {
        playerList.addAll(List.of(player1, player2));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/game/lobby/nickname1"));
        assertEquals(List.of(player2), playerList);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/game/lobby/nickname1"))
                .andExpect(status().isNotFound());

        assertEquals(messageLog.get(0).getType(), "LEAVE");
        assertEquals(messageLog.get(0).getContent(), player1.nickname);
    }

    @Test
    void getLobbyPlayers() throws Exception {
        playerList.addAll(List.of(player1, player2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/game/lobby"))
                .andExpect(content().json("[" + player1InJSON + ", " + player2InJSON + "]"));
    }

    @Test
    void leaveGame() {
        // TODO
    }

    @Test
    void handleMessage() {
        // TODO
    }
}