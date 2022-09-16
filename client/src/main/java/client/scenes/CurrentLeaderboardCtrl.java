package client.scenes;

import client.utils.ServerUtils;
import commons.Player;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class CurrentLeaderboardCtrl implements Initializable {

    @FXML
    private TableColumn<Player, String> playerColumn;

    @FXML
    private TableColumn<Player, Integer> scoreColumn;

    @FXML
    private TableView<Player> table;

    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    /**
     * Constructor for CurrentLeaderboardCtrl
     *
     * @param mainCtrl the MainCtrl to be used
     * @param server   the ServerUtils to be used
     */
    @Inject
    public CurrentLeaderboardCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Method to continue the game with a new question
     */
    @FXML
    void continueGame() {
        mainCtrl.showMultiplayerQuestion();
    }

    /**
     * Initializes the tableview's columns and display a sorted list retrieved from sortLeaderboard()
     *
     * @param location  the URL to be used
     * @param resources the ResourceBundle to be used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerColumn.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().nickname));
        scoreColumn.setCellValueFactory(q -> new ReadOnlyObjectWrapper<>(q.getValue().score));
    }

    /**
     * Refreshes the leaderboard
     */
    public void refresh() {
        table.setItems(sortLeaderboard());
    }

    /**
     * Get list of players from server using ServerUtils and sorts it in descending order
     *
     * @return An observable list op players ordered by score
     */
    private ObservableList<Player> sortLeaderboard() {
        List<Player> list = server.getCurrentLeaderboard(mainCtrl.MPGameId);
        list.sort(Comparator.comparing((Player p) -> p.score).reversed());
        return FXCollections.observableArrayList(list);
    }
}
