package client.scenes;

import client.utils.ServerUtils;
import commons.Message;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MostExpensiveQMultiplayerCtrl extends IQuestionCtrl implements IMultiplayerQuestionCtrl{

    @FXML
    ImageView image1, image2, image3;
    @FXML
    Text choice1, choice2, choice3;

    @FXML
    TableView<String[]> table;

    @FXML
    TableColumn<String[], String> emojiColumn;

    @FXML
    TableColumn<String[], String> playerColumn;

    @FXML
    Button exitButton;

    @FXML
    VBox chatBox;

    @FXML Button heartsEmoji;
    @FXML Button cryingEmoji;
    @FXML Button scaredEmoji;
    @FXML Button skullEmoji;



    /**
     * Constructor for HomeScreenCtrl
     *
     * @param mainCtrl The MainCtrl to be used
     * @param utils    The ServerUtils to be used
     */
    @Inject
    public MostExpensiveQMultiplayerCtrl(MainCtrl mainCtrl, ServerUtils utils) {
        super(mainCtrl, utils);
    }

    public void init() {
        setAnswersDisabled(false);

        byte[] heart = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x8D};
        byte[] crying = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0xAD};
        byte[] scared = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0xB1};
        byte[] skull = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x92, (byte)0x80};

        String emoji1 = new String(heart, Charset.forName("UTF-8"));
        String emoji2 = new String(crying, Charset.forName("UTF-8"));
        String emoji3 = new String(scared, Charset.forName("UTF-8"));
        String emoji4 = new String(skull, Charset.forName("UTF-8"));

        heartsEmoji.setText(emoji1);

        cryingEmoji.setText(emoji2);

        scaredEmoji.setText(emoji3);

        skullEmoji.setText(emoji4);

        // restores initial state of elements
        choice1.setStyle("-fx-fill: black");
        choice2.setStyle("-fx-fill: black");
        choice3.setStyle("-fx-fill: black");

        // initialize fields
        image1.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(0).imagePath))));
        image2.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(1).imagePath))));
        image3.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(2).imagePath))));

        choice1.setText(question.getActivityList().get(0).title);
        choice2.setText(question.getActivityList().get(1).title);
        choice3.setText(question.getActivityList().get(2).title);

        eliminateJokerButton.setDisable(mainCtrl.player.answerJoker);

        mainCtrl.setScene("Please enter your answer", SceneTypes.expensiveMP);
    }

    /**
     * (todo) get's the emoji and passes it on to the server
     * @param e Event used to get emoji
     */
    public void sentEmoji(Event e) {
        String emoji = ((Button) e.getTarget()).getText();
        String player = mainCtrl.player.nickname;

        mainCtrl.ws.send("/app/" + mainCtrl.MPGameId + "/emoji", new Message("EMOJI", player + " sent " + emoji));
    }

    /**
     * add new row with nickname and emoji to tableview
     * @param messages list of String arrays where [0] contains nickname and [1] contains the emoji
     */

    public void refreshTable(List<String[]> messages) {
        table.setItems(FXCollections.observableList(messages));
    }

    public void removeTime(double percentage) {}

    @Override
    public void clearChat() {
        chatBox.getChildren().clear();
    }

    public void updateChat(Message message) {
        Text text = new Text((String) message.getContent());
        chatBox.getChildren().add(0, new Label((String) message.getContent()));

        // prevent emojis from overflowing the VBox container
        if (chatBox.getChildren().size() >= 14) {
            chatBox.getChildren().remove(chatBox.getChildren().size() - 1);
        }
    }

    /**
     * To be implemented
     */
    public void makeChoice(Event e) {
        // Cancel default 20s countdown timer (to not reveal answers twice)
//        cancelTimer();

        // Increment number of questions answered
        // Checks if e is null, because this should only increment once, while the function potentially runs twice
        // Only increment when time is over
        if (e == null) mainCtrl.singleplayerGame.incrementQuestionsAnswered();

        String id = "";
        if (e != null) {
            // Get ID of image clicked and check if it was correct
            id = ((Node) e.getSource()).getId();
        }
        boolean correct = revealAnswer(id); // Correct and wrong answers are also revealed
        if (correct) {
            updateScore(1000);
            utils.addPlayerScoreToGame(mainCtrl.player, mainCtrl.MPGameId);
        }

        mainCtrl.setStageTitle("Please wait for the other players");
        time.setText("Waiting for the other players...");
    }

    /**
     * calculates the score (based on the remaining time) to be added to the player and adds it
     * @param amount
     */
    public void updateScore(int amount) {
        int pointsToAdd = (int) (Math.max(1000 * (1 - timeBar.getProgress()), 500));
        pointsToAdd = (int) (Math.round(pointsToAdd / 10.0) * 10);

        if(doublePoints) {
            pointsToAdd *= 2;
        }

        mainCtrl.player.addScore(pointsToAdd);

        addedScore.setText("+ " + pointsToAdd);
        addedScore.setVisible(true);
        score.setText("Score: " + mainCtrl.player.score);
        mainCtrl.ws.send("/game/" + mainCtrl.MPGameId + "/answer", mainCtrl.player);
    }

    /**
     * Handles logic to determine what answer is correct, colors the question titles
     * according to correctness, and starts a time-out phase until the next question.
     * @param buttonId String of the pressed button id
     * @return true iff guess was correct
     */
    public boolean revealAnswer(String buttonId) {
        // Disable all answers
        setAnswersDisabled(true);

        Long consumption1 = question.getActivityList().get(0).consumptionInWh;
        Long consumption2 = question.getActivityList().get(1).consumptionInWh;
        Long consumption3 = question.getActivityList().get(2).consumptionInWh;

        // initially colors everything red
        choice1.setStyle("-fx-fill: firebrick");
        choice2.setStyle("-fx-fill: firebrick");
        choice3.setStyle("-fx-fill: firebrick");

        boolean correct = false;

        // then colors the right answer green
        if (consumption1 > consumption2 && consumption1 > consumption3) {
            choice1.setStyle("-fx-fill: green");
            if (buttonId.equals("choice1") || buttonId.equals("image1")) {
                correct = true;
            }
        } else if (consumption2 > consumption1 && consumption2 > consumption3) {
            choice2.setStyle("-fx-fill: green");
            if (buttonId.equals("choice2") || buttonId.equals("image2")) {
                correct = true;
            }
        } else {
            choice3.setStyle("-fx-fill: green");
            if (buttonId.equals("choice3") || buttonId.equals("image3")) {
                correct = true;
            }
        }

        return correct;
    }

    /**
     * Cancels the timer and call method showHome
     */
    public void setAnswersDisabled(boolean isDisabled) {
        image1.setDisable(isDisabled);
        image2.setDisable(isDisabled);
        image3.setDisable(isDisabled);
    }

    /**
     * reduces time left for the player
     */
    public void reduceTime() {
        double progress = timeBar.getProgress();
        if (progress < 1d) {
            // Remove half the remaining time
            timeBar.setProgress(progress + (1d - progress) * 0.5d);
        }
    }

    /**
     * Returns the player to the Home screen
     */
    public void goBack() {
        // Cancel default 20s countdown timer (to not reveal answers twice)
        cancelTimer();

        mainCtrl.ws.send("/topic/" + mainCtrl.MPGameId, new Message("LEAVE", mainCtrl.player));
        mainCtrl.ws.close();
        mainCtrl.ws = null;
        // updates current active players not in multiplayer to contain this player
        utils.addPlayer(mainCtrl.player);
        mainCtrl.showHome();
    }

    /**
     * Shows which answer is definitely wrong to the player by coloring that answer red
     */
    public void eliminateAnswer() {
        mainCtrl.player.useAnswerJoker();
        eliminateJokerButton.setDisable(true);

        Long consumption1 = question.getActivityList().get(0).consumptionInWh;
        Long consumption2 = question.getActivityList().get(1).consumptionInWh;
        Long consumption3 = question.getActivityList().get(2).consumptionInWh;

        List<Long> consumptions = new ArrayList<>();
        consumptions.add(consumption1);
        consumptions.add(consumption2);
        consumptions.add(consumption3);

        List<ImageView> images = new ArrayList<>();
        images.add(image1);
        images.add(image2);
        images.add(image3);

        List<Text> texts = new ArrayList<>();
        texts.add(choice1);
        texts.add(choice2);
        texts.add(choice3);

        int maxInd = consumptions.indexOf(Collections.max(consumptions));

        consumptions.remove(maxInd);
        images.remove(maxInd);
        texts.remove(maxInd);

        int i = new Random().nextInt(2);

        images.get(i).setDisable(true);
        texts.get(i).setStyle("-fx-fill: firebrick");
    }

}
