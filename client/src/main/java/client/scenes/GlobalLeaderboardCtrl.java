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

public class GlobalLeaderboardCtrl implements Initializable {

    @FXML
    private TableColumn<Player, String> playerColumn;

    @FXML
    private TableColumn<Player, Integer> scoreColumn;

    @FXML
    private TableView<Player> table;

    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    /**
     * constructor for GlobalLeaderboardCtrl
     *
     * @param mainCtrl to be used for main control over the scenes
     * @param server   serverutilities for communication with the server
     */
    @Inject
    public GlobalLeaderboardCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Method to leave the global leaderboard to home screen
     */
    @FXML
    void goBack() {
        mainCtrl.showHome();
    }

    /**
     * Initializes the tableview's columns and display a sorted list retrieved from sortLeaderboard()
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerColumn.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().nickname));
        scoreColumn.setCellValueFactory(q -> new ReadOnlyObjectWrapper<>(q.getValue().score));
    }

    /**
     * refreshes the leaderboard
     */
    public void refresh() {
        table.setItems(sortLeaderboard());
    }

    /**
     * get list of players from server using serverUtils and sorts it in descending order
     *
     * @return returns an observable list op players ordered by score
     */
    private ObservableList<Player> sortLeaderboard() {
        List<Player> list = server.getLeaderboard();
        list.sort(Comparator.comparing((Player p) -> p.score).reversed());
        return FXCollections.observableArrayList(list);
    }
}
