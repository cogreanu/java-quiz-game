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

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.function.Consumer;

public class NicknameCtrl implements Initializable {

    @FXML
    private TextField nickname;

    @FXML
    private TextField host;

    @FXML
    private Button exitButton;

    @FXML
    private Label invalidText; // Text that appears to inform users when an invalid nickname was entered

    @FXML
    private Label takenText;

    @FXML
    private Label invalidHost; // Text that appears to inform users when a host is invalid

    MainCtrl mainCtrl;
    ServerUtils utils;

    /**
     * Constructor for NicknameCtrl
     *
     * @param mainCtrl the MainCtrl to be used
     */
    @Inject
    public NicknameCtrl(MainCtrl mainCtrl, ServerUtils utils) {
        this.mainCtrl = mainCtrl;
        this.utils = utils;
    }

    private void runOnPlayerFile(Consumer<File> function) {
        String sep = System.getProperty("file.separator");
        String path = System.getProperty("user.dir") + sep +
                "src" + sep +
                "main" + sep +
                "resources" + sep +
                "data" + sep +
                "defaultName.txt";

        function.accept(new File(path));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Checks if a name is present in resources/defaultName.txt
        runOnPlayerFile(file -> {
            try {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNext()) {
                    nickname.setText(scanner.next());
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error while processing file \"defaultName.txt\": file not found at " + file.getAbsolutePath());
            }
        });
    }

    /**
     * Takes you back to the main lobby
     */
    public void goBack() {
        mainCtrl.exitDisplay();
    }

    /**
     * Creates a new player with the nickname chosen by the user
     * After that it clears the fields and shows HomeScreen
     */
    public void save() {
        System.out.println("Saving player: " + nickname.getText());
        runOnPlayerFile(file -> {
            try {
                Scanner scanner = new Scanner(file);
                if (!scanner.hasNext() || !scanner.next().equals(nickname.getText())) {
                    FileWriter writer = new FileWriter(file.getAbsolutePath());
                    writer.write(nickname.getText());
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Error occurred while saving the player's name to the file.");
            }
        });
        mainCtrl.createPlayer(nickname.getText());
        nickname.clear();
        mainCtrl.showHome();
    }

    /**
     * Checks if the nickname chosen is following some rules:
     * It should be at least 3 characters long
     * It should be at most 20 characters long
     */
    public void verify() {
        if (nickname.getLength() < 3 || nickname.getLength() > 20 || !nickname.getText().matches("\\S+")) {
            takenText.setVisible(false);
            invalidText.setVisible(true);
        } else if (utils.nicknameTaken(nickname.getText())) {
            invalidText.setVisible(false);
            takenText.setVisible(true);
        } else {
            System.out.println("request: " + utils.nicknameTaken(nickname.getText()));
            // Username is valid
            if (host.getLength() == 0 || hostValid()) {
                save();
            } else {
                invalidHost.setVisible(true);
            }
        }
    }

    /**
     * TO be implemented: checks hosts validity
     * @return whether the host is valid
     */
    private boolean hostValid() {
        return true;
        // todo check hostname validity when sockets are implemented
    }
}
