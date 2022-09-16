/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import commons.Activity;
import commons.Player;
import commons.Question;
import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {
                });
    }

    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    public List<Activity> getNActivities(int N) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/activities/rnd" + N)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Activity>>() {
                });
    }

    /**
     * adds an activity to the server
     * @param activity activity to add
     * @return the added activity
     */
    public Activity addActivity(Activity activity) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/activities")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    /**
     * updates an activity on the server
     * @param activity with what to update
     * @param id which activity to update based on the id
     * @return the updated activity
     */
    public Activity updateActivity(Activity activity, long id) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/activities/" + id)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

//    public Activity updateActivity(Activity activity) {
//        return ClientBuilder.newClient(new ClientConfig())
//                .target(SERVER).path("api/activities/" + activity.getActivityID())
//                .request(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .put(Entity.entity(activity, APPLICATION_JSON), Activity.class);
//    }

    /**
     * deletes an activity from the server
     * @param activity activity to delete
     */
    public void deleteActivity(Activity activity) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/activities/" + activity.getQuestionId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(new GenericType<>() {
                });
    }

    /**
     * returns all the activities on the server
     * @return list of activities found on the server
     */
    public List<Activity> getActivities() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/activities")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Activity>>() {
                });
    }

    public List<Player> getTopFive() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/leaderboard")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Player>>() {
                });
    }

    /**
     * Returns a question generated by the server side's QuestionService
     *
     * @return A question of random type
     */
    public Question generateQuestion() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/question")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Question>() {
                });
    }

    /**
     * Gets an image from the server
     * @param image which image to get
     * @return byte array containing the image
     */
    public byte[] getImage(String image) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/activities/img/" + image)
                .request(new MediaType("image", "jpeg"))
                .accept(new MediaType("image", "jpeg"))
                .get(new GenericType<byte[]>() {
                });
    }

    /**
     * Joins the current lobby, creating a new {@link Player} with the given nickname
     *
     * @param player The name of the joining Player
     * @return the ID of the lobby
     */
    public Integer joinLobby(Player player) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/lobby")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(player, APPLICATION_JSON), Integer.class);
    }

    /**
     * Causes {@link Player} with the given nickname to leave the lobby
     *
     * @param name The nickname of the player that leaves
     */
    public void leaveLobby(String name) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/lobby/" + name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    /**
     * Retrieves a List of all {@link Player}s in the lobby
     *
     * @return The List of Players
     */
    public List<Player> getLobbyPlayers() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/lobby")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Player>>() {
                });
    }

    /**
     * starts the multiplayer game for all players in the lobby
     * @param player which player started it
     */
    public void startGame(Player player) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/lobby/start")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(player, APPLICATION_JSON));
    }

    /**
     * gets the next question for a multiplayer game from the server
     * @param gameId for which game to get it
     * @return the next question
     */
    public Question getCrtQuestion(int gameId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/" + gameId + "/question")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Question>() {
                });
    }

    /**
     * gets the global leaderboard from the server
     * @return list of players on the leaderboard
     */
    public List<Player> getLeaderboard() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/leaderboard/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Player>>() {
                });
    }

    /**
     * updates the score of a specific player from a specific game
     * @param player player to updates it to (also gets the score from that player)
     * @param gameId in which game that player is based on the gameId
     */
    public void addPlayerScoreToGame(Player player, Integer gameId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/" + gameId + "/updateScore/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(player, APPLICATION_JSON));
    }

    public List<Activity> getAlternative() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/activities/equal")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Activity>>() {
                });

    }

    /**
     * updates the player score for the leaderboard
     * @param player for what player to update it and with what (based on player.score)
     * @return the player of the leaderboard entry with the (if the score was better) new score
     */
    public Player addPlayerScore(Player player) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("/api/leaderboard/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(player, APPLICATION_JSON), Player.class);
    }

    public Integer getGameID() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/getGameID")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Integer>() {
                });
    }

    /**
     * Gets the current leaderboard for a multiplayer game from the server
     * @param gameId for which game to get it based on the gameId
     * @return returns the list of players on that leaderboard
     */
    public List<Player> getCurrentLeaderboard(int gameId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/" + gameId + "/leaderboard")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Player>>() {});
    }

    /**
     * checks whether a nickname is already in use on the server
     * @param nickname nickname to look for
     * @return a boolean whether the nickname is in use
     */
    public boolean nicknameTaken(String nickname) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/" + nickname + "/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Boolean.class);
    }

    /**
     * Adds a player to the currentPlayer list on the server
     * @param player player to add
     * @return the added player
     */
    public Player addPlayer(Player player) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(player, APPLICATION_JSON), Player.class);
    }

    /**
     * Removes a player from the currentPlayer list on the server
     * @param nickname nickname of the player to remove
     */
    public void removePlayer(String nickname) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/" + nickname + "/")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }
}