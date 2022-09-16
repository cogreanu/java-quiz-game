package client.scenes;

import client.SingleplayerGame;
import client.services.ClientGameManager;
import client.services.WebSocketHandler;
import client.utils.ServerUtils;
import commons.Message;
import commons.Player;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class LobbyCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final ServerUtils serverUtils;
    private final ClientGameManager manager;
    private int gameId;

    @FXML
    private TableView<Player> playerTable;
    @FXML
    private TableColumn<Player, String> colColor;
    @FXML
    private TableColumn<Player, String> colNickname;

    @Inject
    public LobbyCtrl(MainCtrl mainCtrl, ServerUtils serverUtils, ClientGameManager manager) {
        this.mainCtrl = mainCtrl;
        this.serverUtils = serverUtils;
        this.manager = manager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colColor.setCellValueFactory(p -> new SimpleStringProperty("\u2B24 " + p.getValue().color));

        // Inspired by an answer found on stack overflow: https://stackoverflow.com/questions/6998551/setting-font-color-of-javafx-tableview-cells
        colColor.setCellFactory(new Callback() {
                                    public Object call(Object param) {
                                        return new TableCell<String, String>() {

                                            @Override
                                            public void updateItem(String item, boolean empty) {
                                                super.updateItem(item, empty);
                                                if (item == null || empty) {
                                                    setText(null);
                                                    setStyle(null);
                                                } else {
                                                    String[] split = item.split(" ");
                                                    item = split[0];
                                                    this.setTextFill(Color.web(split[1]));
                                                    // Get fancy and change color based on data
                                                    setText(item);
                                                }
                                            }
                                        };
                                    }
                                });

        colNickname.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().nickname));
    }

    /**
     * Setup socket connection and register to relevant channels.
     */
    public void createSocketConnection() {
        mainCtrl.ws = new WebSocketHandler("ws://localhost:8080/ws");

        // Subscribe to lobby room, which will broadcast info about players joining/leaving the lobby
        mainCtrl.ws.registerForMessages("/topic/lobby", Message.class, message -> {
            if ("JOIN".equals(message.getType())) {
                System.out.println("Player JOINED with nickname: " + message.getContent());
                refresh();
            } else if ("LEAVE".equals(message.getType())) {
                System.out.println("Player LEFT with nickname: " + message.getContent());
                refresh();
            } else if ("START".equals(message.getType())) {
                Platform.runLater(this::initializeGame);
            }
        });

        // Get which game to subscribe to
        mainCtrl.MPGameId = serverUtils.joinLobby(mainCtrl.player);
    }

    /**
     * Refresh the table of Players in the lobby
     */
    public void refresh() {
        var players = serverUtils.getLobbyPlayers();
        var data = FXCollections.observableList(players);
        playerTable.getItems().clear();
        playerTable.setItems(data);
    }

    /**
     * Turns the current lobby into an active game
     */
    public void startGame() {
        serverUtils.startGame(mainCtrl.player);
    }

    /**
     * Method to be called after a game is started by either the player themselves, or someone else
     */
    private void initializeGame() {
        mainCtrl.clearEmojiChat();

        System.out.println("INITIALIZE GAME");
        mainCtrl.singleplayerGame = new SingleplayerGame();
        mainCtrl.ws.registerForMessages("/topic/" + mainCtrl.MPGameId, Message.class, message -> {
            switch (message.getType()) {
                case "NEXT_ROUND" -> Platform.runLater(mainCtrl::showMultiplayerQuestion);
                case "TIMEJOKER" -> Platform.runLater(() -> mainCtrl.reduceTime(message));
                case "EMOJI" -> Platform.runLater(() -> mainCtrl.updateMessages(message));
                case "LOCAL_LB" -> Platform.runLater(mainCtrl::showCurrentLeaderboard);
                case "END_LB" -> {
                    mainCtrl.ws.close();
                    mainCtrl.ws = null;
                    serverUtils.addPlayerScore(mainCtrl.player);
                    serverUtils.addPlayer(mainCtrl.player);
                    Platform.runLater(mainCtrl::showGlobalLeaderboard);
                }
                default -> throw new IllegalStateException();
            }
        });
    }

    /**
     * Close websocket connection, leave lobby and return to home screen.
     */
    public void goBack() {
        serverUtils.leaveLobby(mainCtrl.player.nickname);
        mainCtrl.ws.close();
        mainCtrl.ws = null;
        mainCtrl.showHome();
    }
}
