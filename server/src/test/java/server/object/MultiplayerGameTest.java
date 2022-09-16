package server.object;

import commons.Activity;
import commons.Player;
import commons.Question;
import commons.QuestionTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.object.exceptions.PlayerNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiplayerGameTest {

    MultiplayerGame sut;
    List<Question> questions;
    List<Activity> activities;

    @BeforeEach
    void setUp() {
        questions = new ArrayList<>();
        activities = new ArrayList<>();

        activities.add(new Activity("id1", "path1", "title1", 1L, "source1"));
        activities.add(new Activity("id2", "path2", "title2", 2L, "source2"));
        activities.add(new Activity("id3", "path3", "title3", 3L, "source3"));
        activities.add(new Activity("id4", "path4", "title4", 4L, "source4"));

        questions.add(new Question(activities.subList(0, 3), QuestionTypes.mostExp));
        questions.add(new Question(activities.subList(1, 4), QuestionTypes.mostExp));
        questions.add(new Question(activities.subList(3, 4), QuestionTypes.estimate));

        sut = new MultiplayerGame(questions);
    }

    @Test
    void getId() {
        int id = sut.getId();
        MultiplayerGame game2 = new MultiplayerGame(questions);
        assertEquals(id + 1, game2.getId());
    }

    @Test
    void getQuestions() {
        assertEquals(questions, sut.getQuestions());
    }

    @Test
    void incrementCrtRound() {
        assertEquals(0, sut.getCrtRound());
        sut.incrementCrtRound();
        assertEquals(1, sut.getCrtRound());
    }

    @Test
    void addPlayer() {
        Player player1 = new Player("nickname1");
        Player player2 = new Player("nickname2");

        assertEquals(List.of(), sut.getPlayerList());
        sut.addPlayer(player1);
        assertEquals(List.of(player1), sut.getPlayerList());
        sut.addPlayer(player2);
        assertEquals(List.of(player2, player1), sut.getPlayerList());

        assertThrows(IllegalArgumentException.class, () -> {
            sut.addPlayer(player1);
        });
    }

    @Test
    void removePlayer() throws PlayerNotFoundException {
        Player player1 = new Player("nickname1");
        Player player2 = new Player("nickname2");

        sut.addPlayer(player1);
        sut.addPlayer(player2);
        assertEquals(List.of(player2, player1), sut.getPlayerList());
        sut.removePlayer(player1.nickname);
        assertEquals(List.of(player2), sut.getPlayerList());

        assertThrows(PlayerNotFoundException.class, () -> {
            sut.removePlayer(player1.nickname);
        });
    }

    @Test
    void hasPlayer() throws PlayerNotFoundException {
        Player player1 = new Player("nickname1");

        assertFalse(sut.hasPlayerWithName(player1.nickname));
        sut.addPlayer(player1);
        assertTrue(sut.hasPlayerWithName(player1.nickname));
        sut.removePlayer(player1.nickname);
        assertFalse(sut.hasPlayerWithName(player1.nickname));
    }

    @Test
    void getPlayerList() {
        Player player1 = new Player("nickname1");
        Player player2 = new Player("nickname2");

        assertEquals(List.of(), sut.getPlayerList());
        sut.addPlayer(player1);
        sut.addPlayer(player2);
        assertEquals(List.of(player2, player1), sut.getPlayerList());
    }

    @Test
    void getPlayer() throws PlayerNotFoundException {
        Player player1 = new Player("nickname1");
        Player player2 = new Player("nickname2");

        assertThrows(PlayerNotFoundException.class, () -> {
            sut.getPlayer(player1.nickname);
        });
        sut.addPlayer(player1);
        assertEquals(player1, sut.getPlayer(player1.nickname));
        sut.addPlayer(player2);
        sut.removePlayer(player1.nickname);
        assertThrows(PlayerNotFoundException.class, () -> {
            sut.getPlayer(player1.nickname);
        });
    }
}