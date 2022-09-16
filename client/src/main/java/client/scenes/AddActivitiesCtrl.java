package client.scenes;

import client.utils.ServerUtils;
import commons.Activity;
import jakarta.ws.rs.WebApplicationException;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import javax.inject.Inject;

public class AddActivitiesCtrl {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;
    public TextField title;
    public TextField id;
    public TextField consumption;
    public TextField source;
    public TextField imagePath;

    /**
     * Constructor for AddActivitiesCtrl
     *
     * @param mainCtrl the MainCtrl to be used
     * @param server   the ServerUtils to be used
     */
    @Inject
    public AddActivitiesCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Return to the admin interface
     */
    public void back() {
        mainCtrl.showAdmin();
    }

    /**
     * Adds the inputted Activity into the database
     */
    public void ok() {
        try {
            server.addActivity(getActivity());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
    }

    /**
     * Reads in the inputted text of the user and returns an
     * Activity made from these input
     *
     * @return an Activity made from the inputs
     */
    private Activity getActivity() {
        var a = id.getText();
        var b = imagePath.getText();
        var c = title.getText();
        var d = Long.parseLong(consumption.getText());
        var e = source.getText();

        return new Activity(a, b, c, d, e);
    }

    private void clearFields() {
        title.clear();
        id.clear();
        consumption.clear();
        source.clear();
        imagePath.clear();
    }
}

