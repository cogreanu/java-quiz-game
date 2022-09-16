package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TableView<Activity> table;
    @FXML
    private TableColumn<Activity, String> colId;
    @FXML
    private TableColumn<Activity, String> colTitle;
    @FXML
    private TableColumn<Activity, String> colConsumption;
    @FXML
    private TableColumn<Activity, String> colSource;
    @FXML
    private TableColumn<Activity, String> colPath;


    @Inject
    public AdminCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().id));
        colId.setCellFactory(TextFieldTableCell.forTableColumn());

        colPath.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().imagePath));
        colPath.setCellFactory(TextFieldTableCell.forTableColumn());

        colTitle.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().title));
        colTitle.setCellFactory(TextFieldTableCell.forTableColumn());

        colConsumption.setCellValueFactory(q -> new SimpleStringProperty(String.valueOf(q.getValue().consumptionInWh)));
        colConsumption.setCellFactory(TextFieldTableCell.forTableColumn());

        colSource.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().source));
        colSource.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * refreshes the activities displayed in the table
     */
    public void refresh() {
        var activities = server.getActivities();
        ObservableList<Activity> data = FXCollections.observableList(activities);
        table.setItems(data);
    }

    /**
     * Deletes an activity
     */
    public void delete() {
        Activity activity = table.getSelectionModel().getSelectedItem();
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete event " +
                    activity.title + "?", ButtonType.YES, ButtonType.NO);
            ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
            if (ButtonType.NO.equals(result))
                return;
            else {
                server.deleteActivity(activity);
                refresh();
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    /**
     * method for editing the question id
     * @param productStringCellEditEvent
     */
    public void editQuestionId(TableColumn.CellEditEvent<Activity, String> productStringCellEditEvent) {
        Activity activity = table.getSelectionModel().getSelectedItem();
        activity.id = productStringCellEditEvent.getNewValue();
        server.updateActivity(activity, activity.getQuestionId());
        refresh();
    }

    /**
     * method for editing the image path
     * @param productStringCellEditEvent
     */
    public void editImagePath(TableColumn.CellEditEvent<Activity, String> productStringCellEditEvent) {
        Activity activity = table.getSelectionModel().getSelectedItem();
        activity.imagePath = productStringCellEditEvent.getNewValue();
        server.updateActivity(activity, activity.getQuestionId());
        refresh();
    }

    /**
     * method for editing the title
     * @param productStringCellEditEvent
     */
    public void editTitle(TableColumn.CellEditEvent<Activity, String> productStringCellEditEvent) {
        Activity activity = table.getSelectionModel().getSelectedItem();
        activity.title = productStringCellEditEvent.getNewValue();
        server.updateActivity(activity, activity.getQuestionId());
        refresh();
    }

    /**
     * method for editing the consumption
     * @param productStringCellEditEvent
     */
    public void editConsumption(TableColumn.CellEditEvent<Activity, String> productStringCellEditEvent) {
        Activity activity = table.getSelectionModel().getSelectedItem();
        activity.consumptionInWh = Long.parseLong(productStringCellEditEvent.getNewValue());
        server.updateActivity(activity, activity.getQuestionId());
        refresh();
    }

    /**
     * method for editing the source
     * @param productStringCellEditEvent
     */
    public void editSource(TableColumn.CellEditEvent<Activity, String> productStringCellEditEvent) {
        Activity activity = table.getSelectionModel().getSelectedItem();
        activity.source = productStringCellEditEvent.getNewValue();
        server.updateActivity(activity, activity.getQuestionId());
        refresh();
    }

    public void back() {
        mainCtrl.showHome();
    }

    public void selectAddActivities() {
        mainCtrl.showAddActivity();
    }

    public void selectEditActivities() {

    }
}
