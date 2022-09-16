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
package client.scenes;

import client.SingleplayerGame;
import client.services.WebSocketHandler;
import client.utils.ServerUtils;
import commons.Message;
import commons.Player;
import commons.Question;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


public class MainCtrl {

    public SingleplayerGame singleplayerGame;
    private Stage primaryStage;
    // initialize them here because it doesn't compile if not
    private Map<SceneTypes, Object> ctrls = new HashMap<>();
    private Map<SceneTypes, Scene> scenes = new HashMap<>();

    public Player player;
    public ServerUtils utils;

    public WebSocketHandler ws;
    public int MPGameId;

    @Inject
    public MainCtrl(ServerUtils utils) {
        this.utils = utils;
    }

    public MainCtrl() {
    }


    /**
     * Initializes all the implemented stages used in the application
     *
     * @param primaryStage     the stage where the application starts
     * @param home             the HomeScreen, see HomeScreenCtr and HomeScreen.fxml
     * @param nickname         the NicknameScreen, see NicknameCtrl and Nickname.fxml
     * @param most             the MostExpensiveQuestion, see MostExpensiveQSingleplayerCtrl, MostExpensiveQSingleplayer.fxml and MostExpensiveQ.java
     * @param mostMultiplayerQ the MostExpensiveQMultiplayer scene, see MostExpensiveQMultiplayerCtrl.java and MostExpensiveQMultiplayer.fxml
     */

    public void initialize(Stage primaryStage, Pair<HomeScreenCtrl, Parent> home,
                           Pair<NicknameCtrl, Parent> nickname,
                           Pair<MostExpensiveQSingleplayerCtrl, Parent> most,
                           Pair<GlobalLeaderboardCtrl, Parent> globalLB,
                           Pair<MostExpensiveQMultiplayerCtrl, Parent> mostMultiplayerQ,
                           Pair<AdminCtrl, Parent> admin,
                           Pair<AddActivitiesCtrl, Parent> addActivities,
                           Pair<LobbyCtrl, Parent> lobby,
                           Pair<HelpScreenCtrl, Parent> helpScreen,
                           Pair<EstimateQSingleplayerCtrl, Parent> estimateQSingleplayer,
                           Pair<MultipleQSingleplayerCtrl, Parent> multipleQSingleplayer,
                           Pair<CurrentLeaderboardCtrl, Parent> currentLB,
                           Pair<AlternativeQSingleplayerCtrl, Parent> alternativeQSingleplayer) {

        //this.ctrls = new HashMap<>();
        //this.scenes = new HashMap<>();

        this.primaryStage = primaryStage;

        ctrls.put(SceneTypes.home, home.getKey());
        scenes.put(SceneTypes.home, new Scene(home.getValue()));

        ctrls.put(SceneTypes.nickname, nickname.getKey());
        scenes.put(SceneTypes.nickname, new Scene(nickname.getValue()));

        ctrls.put(SceneTypes.expensiveSP, most.getKey());
        scenes.put(SceneTypes.expensiveSP, new Scene(most.getValue()));

        ctrls.put(SceneTypes.globalLB, globalLB.getKey());
        scenes.put(SceneTypes.globalLB, new Scene(globalLB.getValue()));

        ctrls.put(SceneTypes.expensiveMP, mostMultiplayerQ.getKey());
        scenes.put(SceneTypes.expensiveMP, new Scene(mostMultiplayerQ.getValue()));

        ctrls.put(SceneTypes.admin, admin.getKey());
        scenes.put(SceneTypes.admin, new Scene(admin.getValue()));

        ctrls.put(SceneTypes.addActivities, addActivities.getKey());
        scenes.put(SceneTypes.addActivities, new Scene(addActivities.getValue()));

        ctrls.put(SceneTypes.helpScreen, helpScreen.getKey());
        scenes.put(SceneTypes.helpScreen, new Scene(helpScreen.getValue()));

        ctrls.put(SceneTypes.lobby, lobby.getKey());
        scenes.put(SceneTypes.lobby, new Scene(lobby.getValue()));

        ctrls.put(SceneTypes.estimateSP, estimateQSingleplayer.getKey());
        scenes.put(SceneTypes.estimateSP, new Scene(estimateQSingleplayer.getValue()));

        ctrls.put(SceneTypes.alternativeSP, alternativeQSingleplayer.getKey());
        scenes.put(SceneTypes.alternativeSP, new Scene(alternativeQSingleplayer.getValue()));

        ctrls.put(SceneTypes.multipleSP, multipleQSingleplayer.getKey());
        scenes.put(SceneTypes.multipleSP, new Scene(multipleQSingleplayer.getValue()));

        ctrls.put(SceneTypes.currentLB, currentLB.getKey());
        scenes.put(SceneTypes.currentLB, new Scene(currentLB.getValue()));

        this.singleplayerGame = null;
        this.ws = null;

        showNickname();
        primaryStage.show();
    }


    /**
     * Change the stage title to the given title
     *
     * @param title the title to which it is changed
     */
    public void setStageTitle(String title) {
        primaryStage.setTitle(title);
    }

    /**
     * Changes the stage's title and scene to the ones provided in parameters
     *
     * @param title The title of the stage
     * @param type  The type of scene to switch to
     */
    public void setScene(String title, SceneTypes type) {
        primaryStage.setTitle(title);
        primaryStage.setScene(scenes.get(type));
    }

    /**
     * Creates a player with given nickname
     *
     * @param nickname the nickname of the player
     */

    public void createPlayer(String nickname) {
        String n = String.valueOf(nickname);
        this.player = new Player(n);
        utils.addPlayer(this.player);
        ((HomeScreenCtrl) this.ctrls.get(SceneTypes.home)).setNicknameText(nickname);
    }

    /**
     * Shows the home/splash screen
     */
    public void showHome() {
        this.primaryStage.setTitle("Home screen");
        this.primaryStage.setScene(scenes.get(SceneTypes.home));
    }

    /**
     * Shows the screen where user can enter a nickname
     */
    public void showNickname() {
        primaryStage.setTitle("Enter your nickname");
        primaryStage.setScene(scenes.get(SceneTypes.nickname));
    }

    public void showLobby() {
        ((LobbyCtrl) ctrls.get(SceneTypes.lobby)).createSocketConnection();
        primaryStage.setTitle("Waiting for players...");
        primaryStage.setScene(scenes.get(SceneTypes.lobby));
    }

    /**
     * Shows the admin interface
     */
    public void showAdmin() {
        primaryStage.setTitle("Admin interface");
        primaryStage.setScene(scenes.get(SceneTypes.admin));
    }

    /**
     * Shows the addActivity screen
     */
    public void showAddActivity() {
        primaryStage.setTitle("Add activities");
        primaryStage.setScene(scenes.get(SceneTypes.addActivities));
    }

    /**
     * Shows the user a random question
     */
    public void showQuestion() {
        Question question = utils.generateQuestion();

        switch (question.getType()) {
            case mostExp -> ((IQuestionCtrl) ctrls.get(SceneTypes.expensiveSP)).generalInitialize(question);
            case estimate -> ((IQuestionCtrl) ctrls.get(SceneTypes.estimateSP)).generalInitialize(question);
            case alternative -> ((IQuestionCtrl) ctrls.get(SceneTypes.alternativeSP)).generalInitialize(question);
            case multiple -> ((IQuestionCtrl) ctrls.get(SceneTypes.multipleSP)).generalInitialize(question);
            default -> throw new IllegalStateException("Unexpected value: " + question.getType());
        }
    }

    /**
     * Sets the scene for the given question
     */
    public void showMultiplayerQuestion() {
        Question question = utils.getCrtQuestion(MPGameId);

        System.out.println("Showing new multiplayer question");

        switch (question.getType()) {
            case mostExp -> ((IQuestionCtrl) ctrls.get(SceneTypes.expensiveMP)).generalInitialize(question);
//            case estimate -> ((IQuestionCtrl) ctrls.get(SceneTypes.estimateMP)).generalInitialize(question);
            default -> throw new IllegalStateException("Unexpected value: " + question.getType());
        }
    }

    public void updateMessages(Message message) {
        ((MostExpensiveQMultiplayerCtrl) ctrls.get(SceneTypes.expensiveMP)).updateChat(message);
    }

    public void reduceTime(Message message) {
        if (!player.nickname.equals(((Player) message.getContent()).nickname)) {
            ((MostExpensiveQMultiplayerCtrl) ctrls.get(SceneTypes.expensiveMP)).reduceTime();
        }
    }

    /**
     * Shows the Global Leaderboard
     */
    public void showGlobalLeaderboard() {
        primaryStage.setTitle("Global Leaderboard");
        ((GlobalLeaderboardCtrl) ctrls.get(SceneTypes.globalLB)).refresh();
        primaryStage.setScene(scenes.get(SceneTypes.globalLB));
    }

    /**
     * Shows the help screen
     */
    public void showHelpScreen() {
        primaryStage.setTitle("How to play");
        primaryStage.setScene(scenes.get(SceneTypes.helpScreen));
    }

    /**
     * Shows the Current Leaderboard
     */
    public void showCurrentLeaderboard() {
        primaryStage.setTitle("Current Leaderboard");
        ((CurrentLeaderboardCtrl) ctrls.get(SceneTypes.currentLB)).refresh();
        primaryStage.setScene(scenes.get(SceneTypes.currentLB));
    }

    /**
     * Method that clears the emoji chat of currently implemented multiplayer scenes
     */
    public void clearEmojiChat() {
        ((MostExpensiveQMultiplayerCtrl) ctrls.get(SceneTypes.expensiveMP)).clearChat();
    }

    /**
     * Method used to give the user a last reminder before exiting the game
     */
    public void exitDisplay() {
        // delete player from server record
        if (this.player != null) {
            utils.removePlayer(this.player.nickname);
        }

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Exit confirmation");
        window.setMaxWidth(650);


        Label label = new Label();
        label.setText("Are you sure you want to exit the game?");
        Button button = new Button("Close game");
        button.setId("exitButton");
        Button b = new Button("Remain in game");
        b.setId("continueButton");
        button.setOnAction(e -> Platform.exit());
        b.setOnAction(e -> window.close());

        VBox layout = new VBox(20);
        layout.setPrefHeight(200);
        layout.setPrefWidth(250);
        layout.getChildren().addAll(label, button, b);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.getScene().getStylesheets().add("stylesheets/general.css");
        window.showAndWait();

    }

//    /**
//     * method used to go give last reminder before going to main
//     */
//    public static void backToMain(){
//        Stage window = new Stage();
//        window.initModality(Modality.APPLICATION_MODAL);
//        window.setTitle("Exit confirmation");
//        window.setMaxWidth(650);
//
//        Label label = new Label();
//        label.setText("Are you sure you want to exit the game?");
//        Button button = new Button("Back to lobby");
//        Button b = new Button("Remain in game");
//        button.setOnAction(e -> mainCtrl.showHome());
//        b.setOnAction(e -> window.close());
//        // button.setOnAction(e -> window.close());
//
//        VBox layout = new VBox(50);
//        layout.getChildren().addAll(label,button, b);
//        layout.setAlignment(Pos.CENTER);
//
//        Scene scene = new Scene(layout);
//        window.setScene(scene);
//        window.showAndWait();
//
//    }
}

// Template code used for reference only
// public void showOverview() {
// primaryStage.setTitle("Quotes: Overview");
// primaryStage.setScene(overview);
// overviewCtrl.refresh();
// }

// public void showAdd() {
// primaryStage.setTitle("Quotes: Adding Quote");
// primaryStage.setScene(add);
// add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
// }