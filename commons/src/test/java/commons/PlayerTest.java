package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PlayerTest {

    private Player player;
    private Player player1;

    private int score;


    @BeforeEach
    public void setUp() {
        this.player = new Player("p1");
        this.player1 = new Player("p1");
        this.score = 0;
    }

    @Test
    public void addScoreTest() {
        int am = 3;
        player.addScore(am);
        assertEquals(3, player.score);
    }

    @Test
    public void equalsTest() {
        assertTrue(player.equals(player1));
    }

    @Test
    public void hashTest() {
        Player p = new Player(23l, "nickname", 3);
        int hash = p.hashCode();
        assertEquals(hash, p.hashCode());
    }


}