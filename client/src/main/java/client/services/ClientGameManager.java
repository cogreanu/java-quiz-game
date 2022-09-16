package client.services;

import client.SingleplayerGame;
import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.Question;

import javax.inject.Inject;

/**
 * Class responsible with managing the Multiplayer Game on the client side
 */

public class ClientGameManager {

    private Question crtQuestion;
    private MainCtrl mainCtrl;
    private ServerUtils utils;
    private int gameId;

    @Inject
    public ClientGameManager(MainCtrl mainCtrl, ServerUtils utils) {
        this.mainCtrl = mainCtrl;
        this.utils = utils;
    }

    public void startNewGame(int gameId) {
        this.gameId = gameId;
        mainCtrl.singleplayerGame = new SingleplayerGame();
        showQuestion();
    }

    public void showQuestion() {
        if (mainCtrl.singleplayerGame.lastQuestionReached()) {
            mainCtrl.showHome();
        }

//        mainCtrl.singleplayerGame.incrementQuestionsAnswered();
//        mainCtrl.showMultiplayerQuestion(utils.getCrtQuestion(gameId));
    }
}
