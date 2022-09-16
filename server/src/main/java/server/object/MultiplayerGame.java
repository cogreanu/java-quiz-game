package server.object;

import commons.Player;
import commons.Question;
import server.object.exceptions.PlayerNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class MultiplayerGame {
    private final Map<String, Player> players; // Nickname mapped to player objects
    private final List<Question> questions;
    private int crtRound = 0;
    private final int questionCount;
    private final int id;
    public List<String> chatHistory;


    private static int nextId = 1;

    public MultiplayerGame(List<Question> questions) {
        this.players = new HashMap<>();
        this.questions = questions;
        this.questionCount = questions.size();
        this.id = nextId;
        nextId++;
    }

    /**
     * Returns the ID of the game
     *
     * @return the game's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the list of questions the game has
     *
     * @return the list of questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Add 1 to the round counter
     */
    public void incrementCrtRound() {
        crtRound++;
    }

    /**
     * Returns the difference between the no. of questions and the crtRound, i.e. how many rounds are left
     *
     * @return the no. of remaining rounds
     */
    public int getCrtRound() {
        return crtRound;
    }

    /**
     * Adds a {@link Player} to the game
     *
     * @param player the player to be added
     * @throws IllegalArgumentException if a Player with the same nickname is already in the game
     */
    public void addPlayer(Player player) throws IllegalArgumentException {
        if (hasPlayerWithName(player.nickname)) {
            throw new IllegalArgumentException("Player " + player.nickname + " already in game " + id);
        }
        players.put(player.nickname, player);
    }

    /**
     * Removes the player associated to the nickname from the game
     * @param nickname nickname to look for
     * @return the player that got removed
     * @throws PlayerNotFoundException if the player based on that nickname isn't found
     */
    public Player removePlayer(String nickname) throws PlayerNotFoundException {
        if (!hasPlayerWithName(nickname)) {
            throw new PlayerNotFoundException("Player " + nickname + " not found in game " + id);
        }

        return players.remove(nickname);
    }

    /**
     * Checks if {@link Player} with nickname is in the game
     *
     * @param nickname the nickname to look for
     * @return True if the game has a player with that nickname
     */
    public boolean hasPlayerWithName(String nickname) {
        return players.containsKey(nickname);
    }

    /**
     * Returns the list of {@link Player}s in the Game
     *
     * @return the list of Players
     */
    public List<Player> getPlayerList() {
        return new ArrayList<>(players.values());
    }

    /**
     * Returns the {@link Player} with corresponding nickname
     *
     * @param nickname The nickname of the Player
     * @return the Player object with the given nickname
     * @throws PlayerNotFoundException if there is no key with the value of the nickname in the game
     */
    public Player getPlayer(String nickname) throws PlayerNotFoundException {
        if (!players.containsKey(nickname))
            throw new PlayerNotFoundException("Players with nickname " + nickname + " not found in game " + id);
        return players.get(nickname);
    }

    /**
     * Updates the score of a player in this game
     * @param player for which player to update
     */
    public void updatePlayerScore(Player player) {
        players.put(player.nickname, player);
    }

    public List<Player> getCurrentLeaderboard() {
        return players.values().stream().sorted(Comparator.comparingInt(o -> o.score)).collect(Collectors.toList());
    }
}