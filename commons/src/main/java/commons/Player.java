package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;
import java.util.Random;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String nickname;
    public int score;
    public boolean doubleJoker;
    public boolean timeJoker;
    public boolean answerJoker;
    public String color;


    public Player() {
    }

    /**
     * Constructor for Player with initial score of 0
     *
     * @param nickname the nickname
     */
    public Player(String nickname) {
        this.nickname = nickname;
        this.score = 0;
        this.doubleJoker = false;
        this.timeJoker = false;
        this.answerJoker = false;
        this.color = generateColor();
    }


    public void useDoubleJoker() {
        this.doubleJoker = true;
    }

    public void useTimeJoker() {
        this.timeJoker = true;
    }

    public void useAnswerJoker() {
        this.answerJoker = true;
    }

    /**
     * constructor for the hash method
     */
    public Player(Long id, String nickname, int score) {
        this.id = id;
        this.nickname = nickname;
        this.score = score;

    }

    /*
    if you want to activate the double time joker, just use a simple true boolean to do so
    Params: joker- the state of the double time joker
     */
    public void useDoubleJoker(boolean joker) {
        this.doubleJoker = joker;
    }

    /*
    if you want to activate the remove wrong answer joker, just use a simple true boolean to do so
    Params: joker- the state of the remove wrong answer joker
     */
    public void useAnswerJoker(boolean joker) {
        this.answerJoker = joker;
    }

    /*
    if you want to activate the time joker(decreases the remaining time to answer for the other players by a set amount),
    just use a simple true boolean to do so
    Params: joker- the state of the time joker
     */
    public void useTimeJoker(boolean joker) {
        this.timeJoker = joker;
    }

    /**
     * Increments the score of the player
     *
     * @param amount the amount that the score should be implemented
     */
    public void addScore(int amount) {
        this.score += amount;
    }

    /**
     * Sets the score to a set value
     * @param value The new value of the score
     */
    public void setScore(int value) {
        this.score = value;
    }


    public void doubleScore(int amount) {
        this.score = score + amount * 2;
    }

    public static String generateColor() {
        Random random = new Random();

        int nextInt = random.nextInt(0xffffff + 1);

        // format it as hexadecimal string (with hashtag and leading zeros)
        String color = String.format("#%06x", nextInt);

        return color;
    }

    /**
     * Resets the score of the Player
     */
    public void resetScore() {
        this.score = 0;
    }

    /**
     * Reset all variables for a new game
     */
    public void resetPlayer() {
        resetScore();
        this.doubleJoker = false;
        this.answerJoker = false;
        this.timeJoker = false;
    }

    /**
     * Compares this Player to another object
     *
     * @param o the object to compare with
     * @return true iff o equals this Player
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id && score == player.score && Objects.equals(nickname, player.nickname);
    }

    /**
     * Hashes the Player
     *
     * @return the hashCode created
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, score);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", score=" + score +
                '}';
    }
}
