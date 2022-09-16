package server.api;


import commons.Message;
import commons.Player;
import commons.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import server.object.MultiplayerGame;
import server.object.exceptions.GameNotFoundException;
import server.object.exceptions.PlayerNotFoundException;
import server.services.GameManager;

import java.time.Instant;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that handles the endpoints related to a multiplayer game
 */

@RestController
@RequestMapping("/api/game")
public class MultiplayerHandler {

    private final static Logger logger = LoggerFactory.getLogger(MultiplayerHandler.class);
    private final SimpMessagingTemplate msg;
    private final GameManager manager;

    public MultiplayerHandler(SimpMessagingTemplate msg, GameManager manager) {
        this.msg = msg;
        this.manager = manager;
    }

    /**
     * Rest GET mapping to retrieve gameID of current free game/lobby.
     *
     * @return ResponseEntity with integer of the game ID
     */
    @PutMapping(path = "lobby")
    public ResponseEntity<Integer> joinLobby(@RequestBody Player player) {
        try {
            manager.addToLobby(player);
            manager.getCurrentPlayers().remove(player);
            logger.info("Player " + player.nickname + " joined the lobby");
            msg.convertAndSend("/topic/lobby", new Message("JOIN", player.nickname));
            return ResponseEntity.ok(manager.getLobbyId());
        } catch (IllegalArgumentException e) {
            logger.warn("Player " + player.nickname + " tried to join the lobby, but nickname is already in use");
            throw new ResponseStatusException(HttpStatus.IM_USED, "Nickname already taken");
        }
    }

    /**
     * Calls GameManager.nicknameTaken to determine whether the nickname is already in use
     * @param nickname nickname to look for
     * @return boolean of whether or not the nickname is taken
     */
    @GetMapping(path = "{nickname}")
    public boolean nicknameTaken(@PathVariable String nickname) {
        return manager.nicknameTaken(nickname);
    }

    /**
     * Adds the player to the currentPlayer list of GameManager
     * @param player player to add
     */
    @PostMapping(path = "/")
    public void addPlayer(@RequestBody Player player) {
        manager.getCurrentPlayers().add(player);
    }

    /**
     * removes the player from the currentPlayer list of GameManager based on the nickname
     * @param nickname nickname to look for
     */
    @DeleteMapping(path = "{nickname}/")
    public void removePlayer(@PathVariable String nickname) {
        manager.getCurrentPlayers().removeIf(p -> p.nickname.equals(nickname));
    }

    /**
     * Removes player from lobby
     *
     * @param nickname The nickname of the player to be removed
     */
    @DeleteMapping(path = "lobby/{nickname}")
    public void leaveLobby(@PathVariable String nickname) {
        try {
            manager.removeFromLobby(nickname);
            logger.info("Player " + nickname + " removed from lobby");
            msg.convertAndSend("/topic/lobby", new Message("LEAVE", nickname));
//            return ResponseEntity.ok().build();
        } catch (PlayerNotFoundException e) {
            logger.warn("Player " + nickname + " not found in lobby while attempting to leave");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Returns the players currently in the lobby
     *
     * @return a List of all players in the lobby
     */
    @GetMapping(path = "lobby")
    public List<Player> getLobbyPlayers() {
        return manager.getPlayersInLobby();
    }

    /**
     * Changes the current lobby into an active game, informs all players connected of this, and
     * creates a new lobby for future connections
     */
    @PostMapping(path = "lobby/start")
    public void startGame() throws InterruptedException {
        int lobbyId = manager.getLobbyId();
        msg.convertAndSend("/topic/lobby", new Message("START", null));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                manager.startRound(manager.getLobbyId());
                manager.createNewLobby();
            }
        }, 1000);
    }

    /**
     * Removes Player from a game with the given id
     *
     * @param gameId   The id of the game
     * @param nickname The nickname of the Player
     */
    @DeleteMapping(path = "{gameId}")
    public void leaveGame(@PathVariable Integer gameId, @RequestBody String nickname) {
        try {
            manager.removeFromGame(gameId, nickname);
        } catch (GameNotFoundException | PlayerNotFoundException e) {
            logger.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get the current question being provided by the game with id of gameId
     *
     * @param gameId The id of the game
     * @return The {@link Question}
     * @throws GameNotFoundException if a game with the given gameId doesn't exist
     */
    @GetMapping(path = "/{gameId}/question")
    public Question getCrtQuestion(@PathVariable Integer gameId) throws GameNotFoundException {
        MultiplayerGame game = manager.getGameById(gameId);
        return game.getQuestions().get(game.getCrtRound());
    }

    /**
     * Get endpoint for getting the local leaderboard for a game
     * @param gameId for which game to get the leaderboard for
     * @return returns the leaderboard as a list of players
     * @throws GameNotFoundException thrown when a game isn't found based on gameId
     */
    @GetMapping(path = "{gameId}/leaderboard")
    public List<Player> getLocalLeaderboard(@PathVariable Integer gameId) throws GameNotFoundException {
        return manager.getGameById(gameId).getPlayerList();
    }

    /**
     * Put endpoint for updating a players score
     * @param gameId which game the player is in
     * @param player for which player to update
     * @throws GameNotFoundException thrown when a game isn't found based on gameId
     * @throws PlayerNotFoundException thrown when a player isn't found for that game with that player
     */
    @PutMapping(path = "{gameId}/updateScore")
    public void updatePlayerScore(@PathVariable Integer gameId, @RequestBody Player player) throws GameNotFoundException, PlayerNotFoundException {
        MultiplayerGame game = manager.getGameById(gameId);
        game.getPlayer(player.nickname).setScore(player.score);
    }

    /**
     * get's the current UTC time + 10 seconds for a sychronized start between all players
     *
     * @return returns the time as a long
     */
    @MessageMapping("/game/{gameId}/start")
    @SendTo("/game/{gameId}/startTime")
    public long Start() {
        return Instant.now().toEpochMilli() + 10000;
    }

    /**
     * Sends the username of the player who played a timejoker in the path /timeJoker
     *
     * @param username the username of the player who played the time joker
     * @return returns the username as a String so that the joker only affects other players
     */
    @MessageMapping("/game/{gameId}/timeJoker/{username}")
    @SendTo("/game/{gameId}/timeJoker/")
    public String timeJoker(@DestinationVariable String username) {
        return username;
    }

    /**
     * Adds a sent message ("nickname":"emoji") to the chat history
     *
     * @param gameId Identifier for which game to use
     * @param message    the variable containing the sent message in string form
     * @return returns the chat history of that game as a list of strings
     */
    @MessageMapping("/{gameId}/emoji")
//    @SendTo("/game/{gameId}/chatHistory")
    public void addMsg(@DestinationVariable int gameId, Message message) throws GameNotFoundException {
//        MultiplayerGame game = manager.getGameById(gameId);
//        game.chatHistory.add(message);
        System.out.println("Received emoji");
        msg.convertAndSend("/topic/" + gameId, message);
    }

    /**
     * Check's the answer depending on username and returns the score that should be added to the score of that player
     *
     * @param gameId Identifier for which game to use
     * @param player what player submitted
     * @return returns the score to be added as a long
     */
    @MessageMapping("/topic/{gameId}/answer")
//    @SendTo("/topic/{gameId}/score/{username}")
    public void checkAnswer(@DestinationVariable int gameId, Player player) throws GameNotFoundException {
        System.out.println("Received new score.");
        manager.updatePlayerScore(gameId, player);
    }

    /**
     * gets a random gameId from the hashtable
     *
     * @return returns the randomId
     */
    @MessageMapping("/joinRandom")
    @SendTo("/join")
    public int joinRandom() {
//        return (int) games.keySet().toArray()[random.nextInt(games.size())];
        // THIS IS NOT NEEDED, AS PLAYERS SHOULD ONLY BE ABLE TO JOIN THE LOBBY
        return 0;
    }

    /**
     * gets the next question of the game
     *
     * @param gameId Identifier for which game to use
     * @return returns the next question in the list
     */
    @MessageMapping("/game/{gameId}/nextQuestion")
    @SendTo("/game/{gameId}/question")
    public Question nextQuestion(@DestinationVariable int gameId) throws GameNotFoundException {
        MultiplayerGame game = manager.getGameById(gameId);
        return game.getQuestions().get(game.getCrtRound());
    }

    /**
     * Mapping for socket messages for player actions such as joining or leaving a game.
     *
     * @param msg payload of type Message
     */
    @MessageMapping("/game/lobby")
    @SendTo("/topic/lobby")
    public Message handleMessage(Message msg) {
        return msg;
    }
}