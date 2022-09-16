package server.services;

import commons.Message;
import commons.Player;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import server.object.MultiplayerGame;
import server.object.exceptions.GameNotFoundException;
import server.object.exceptions.PlayerNotFoundException;

import java.util.*;

/**
 * Class that handles game logic
 */

@Service
public class GameManager {

    private final QuestionService qs;
    private final SimpMessagingTemplate msg;

    // List of all current players not in multiplayer (so singleplayer or idle)
    private List<Player> currentPlayers;
    // Always keeps track of current open lobby, where new players are added (until someone presses play)
    private MultiplayerGame lobbyGame;
    // Keeps track of open connections and their respective game
    private Map<Integer, MultiplayerGame> games;

    public static final int questionCount = 20;


    public GameManager(QuestionService qs, SimpMessagingTemplate msg) {
        this.qs = qs;
        this.msg = msg;
        this.games = new HashMap<>();
        this.currentPlayers = new ArrayList<>();

        // TODO: try to use dependency injection
        createNewLobby();
    }

    /**
     * Adds the given {@link Player} to the lobby
     *
     * @param player The Player to be added
     */
    public void addToLobby(Player player) throws IllegalArgumentException {
        lobbyGame.addPlayer(player);
    }

    /**
     * Removes the {@link Player} with the given nickname from the lobby
     *
     * @param nickname The nickname of the Player
     * @throws PlayerNotFoundException if the Player can't be found in the lobby
     */
    public void removeFromLobby(String nickname) throws PlayerNotFoundException {
        currentPlayers.add(lobbyGame.removePlayer(nickname));
    }

    /**
     * Returns a List of all {@link Player}s in the lobby
     *
     * @return The List of Players
     */
    public List<Player> getPlayersInLobby() {
        return lobbyGame.getPlayerList();
    }

    /**
     * Returns the id of the current lobby
     *
     * @return the lobby's id
     */
    public int getLobbyId() {
        return lobbyGame.getId();
    }

    /**
     * Checks if game with given id exists
     *
     * @param id To check
     * @return true if game exists
     */
    public boolean hasGameWithId(int id) {
        return games.containsKey(id);
    }

    /**
     * Returns the {@link MultiplayerGame} with the given id if it exists
     *
     * @param id The id of the game
     * @return The {@link MultiplayerGame} with the given id
     * @throws GameNotFoundException if a game with the given id can't be found
     */
    public MultiplayerGame getGameById(int id) throws GameNotFoundException {
        if (!hasGameWithId(id)) {
            throw new GameNotFoundException("Game with id " + id + " not found");
        }
        return games.get(id);
    }

    /**
     * Removes a Player from a game stored in the {@code games} Map
     *
     * @param id       The id of the game to remove the Player from
     * @param nickname The nickname of the Player
     * @throws GameNotFoundException   if a Game with the given id can't be found
     * @throws PlayerNotFoundException if a Player with the given nickname can't be found in the Game
     */
    public void removeFromGame(int id, String nickname) throws GameNotFoundException, PlayerNotFoundException {
        if (!hasGameWithId(id)) {
            throw new GameNotFoundException("Game with id " + id + " not found");
        }
        var game = games.get(id);
        game.removePlayer(nickname);
    }

    /**
     * Replaces the current lobby with a new one
     */
    public void createNewLobby() {
        lobbyGame = new MultiplayerGame(qs.generateNQuestions(questionCount,"multiplayer"));
        games.put(lobbyGame.getId(), lobbyGame);
    }

    /**
     * Starts the game with the given id
     *
     * @param id The id of the game
     */
    public void startRound(int id) {
        MultiplayerGame game = games.get(id);
        if (game.getCrtRound() == 20) {
            msg.convertAndSend("/topic/" + id, new Message("END_LB", null));
            return;
        }

        msg.convertAndSend("/topic/" + id, new Message("NEXT_ROUND", null));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                msg.convertAndSend("/topic/" + id, new Message("PAUSE", null));
                pauseRound(id);
            }
        }, 10000);
    }

    /**
     * Helper method for {@code startRound()}; mimics the 3-second pause in between rounds
     *
     * @param id the id of the game
     */
    private void pauseRound(int id) {
        MultiplayerGame game = games.get(id);
        msg.convertAndSend("/topic/" + id, new Message("PAUSE", null));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                game.incrementCrtRound();
                if (game.getCrtRound() == 10) {
                    showLocalLB(id);
                } else {
                    startRound(id);
                }
            }
        }, 3000);
    }

    /**
     * Helper method that shows the local leaderboard to the players in a given game
     * @param id The id of the game
     */
    private void showLocalLB(int id) {
        MultiplayerGame game = games.get(id);
        msg.convertAndSend("/topic/" + id, new Message("LOCAL_LB", null));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startRound(id);
            }
        }, 3000);
    }

    /**
     * Checks whether any player already has that nickname
     * @param nickname nickname to check for
     * @return boolean representing whether or not the nickname is taken
     */
    public boolean nicknameTaken(String nickname) {
        List<String> nicknames = new ArrayList<>();
        for (Player player : lobbyGame.getPlayerList()) {
            nicknames.add(player.nickname);
        }
        for (Player player : currentPlayers) {
            nicknames.add(player.nickname);
        }
        for (MultiplayerGame game : games.values()) {
            for (Player player : game.getPlayerList()) {
                nicknames.add(player.nickname);
            }
        }
        for (String name : nicknames) {
            if(name.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get's the currentPlayers that are either idle or in singleplayer list
     * @return List of players
     */
    public List<Player> getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * Updates the players score
     * @param gameId which game the player is in
     * @param player for which player to update the score and with how much (as part of class Player)
     */
    public void updatePlayerScore(int gameId, Player player) {
        try {
            MultiplayerGame game = getGameById(gameId);
            game.updatePlayerScore(player);
        } catch (GameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
