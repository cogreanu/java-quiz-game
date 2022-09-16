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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;

public class HomeScreenCtrl {

    private MainCtrl mainCtrl;
    @FXML
    Button exitButton;

    //changed from label to string since label automatically became null and was impossible to use or test
    @FXML
    private Label nicknameText;

    /**
     * Constructor for HomeScreenCtrl
     *
     * @param mct the MainCtrl to be used
     */
    @Inject
    public HomeScreenCtrl(MainCtrl mct, Label l) {
        this.mainCtrl = mct;
        this.nicknameText = l;
    }

    public Label getNicknameText() {
        return nicknameText;
    }

    /**
     * Sets the text on screen to the new nickname
     *
     * @param nickname the nickname entered by the user
     */
    public void setNicknameText(String nickname) {
        nicknameText.setText("Nickname: " + nickname);
    }

    /**
     * Calls the showQuestion method from mainCtrl
     */
    public void selectSinglePlayer() {
        mainCtrl.singleplayerGame = new SingleplayerGame();
        mainCtrl.player.resetPlayer();
        mainCtrl.showQuestion();
    }

    /**
     * To be implemented
     */
    public void selectMultiPlayer() {
        mainCtrl.player.resetPlayer();
        mainCtrl.showLobby();
    }

    /**
     * To be implemented
     */
    public void selectHelp() {
        mainCtrl.showHelpScreen();
    }

    /**
     * To be implemented
     */
    public void selectLeaderboard() {
        mainCtrl.showGlobalLeaderboard();
    }

    public void selectAdminInterface() {
        mainCtrl.showAdmin();
    }

    public void goBack() {
        mainCtrl.exitDisplay();
    }
}