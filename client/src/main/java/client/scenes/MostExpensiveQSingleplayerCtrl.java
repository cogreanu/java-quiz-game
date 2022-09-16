package client.scenes;

import client.utils.ServerUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MostExpensiveQSingleplayerCtrl extends IQuestionCtrl {

    @FXML
    ImageView image1, image2, image3;
    @FXML
    Text choice1, choice2, choice3;

    @Inject
    MostExpensiveQSingleplayerCtrl(MainCtrl mainCtrl, ServerUtils utils) {
        super(mainCtrl, utils);
    }

    /**
     * This method does the following:
     * <p><ul>
     * <li> restores what changes might have been made in previous rounds
     * <li> initializes the fields with the correct titles / images
     * <li> starts a timer that calls {@link #revealAnswer(String buttonId)} once time elapses
     * </ul></p>
     */
    public void init() {
        // restores initial state of elements
        choice1.setStyle("-fx-fill: black");
        choice2.setStyle("-fx-fill: black");
        choice3.setStyle("-fx-fill: black");
        time.setText("Time left:");

        // initialize fields
        image1.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(0).imagePath))));
        image2.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(1).imagePath))));
        image3.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(2).imagePath))));

        choice1.setText(question.getActivityList().get(0).title);
        choice2.setText(question.getActivityList().get(1).title);
        choice3.setText(question.getActivityList().get(2).title);

        mainCtrl.setScene("Please enter your answer", SceneTypes.expensiveSP);
    }

    /**
     * To be implemented
     */
    public void makeChoice(Event e) {
        // Cancel default 20s countdown timer (to not reveal answers twice)
        cancelTimer();

        // Increment number of questions answered
        mainCtrl.singleplayerGame.incrementQuestionsAnswered();

        String id = "";
        if (e != null) {
            // Get ID of image clicked and check if it was correct
            id = ((Node) e.getSource()).getId();
        }
        boolean correct = revealAnswer(id); // Correct and wrong answers are also revealed
        if (correct) {
            updateScore(1000);
        }

        mainCtrl.setStageTitle("Please wait for the next question");
        time.setText("Wait for next question...");
        startTimer(3.0, () -> {
            setAnswersDisabled(false); // Enable question answers again
            // Logic to check if the game is over or not
            if (mainCtrl.singleplayerGame.lastQuestionReached()) {
                utils.addPlayerScore(mainCtrl.player);
                mainCtrl.showGlobalLeaderboard();
            } else {
                mainCtrl.showQuestion();
            }
        });
    }

    /**
     * Handles logic to determine what answer is correct, colors the question titles
     * according to correctness, and starts a time-out phase until the next question.
     *
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
