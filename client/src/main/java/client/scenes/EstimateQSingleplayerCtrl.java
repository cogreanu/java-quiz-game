package client.scenes;

import client.utils.ServerUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;

public class EstimateQSingleplayerCtrl extends IQuestionCtrl {

    @FXML
    TextField answerField;
    @FXML
    ImageView questionImage;
    @FXML
    Button submitButton;
    @FXML
    Text questionText;
    @FXML
    Text wrongInputText;
    @FXML
    Text revealAnswerText;

    @Inject
    public EstimateQSingleplayerCtrl(MainCtrl mainCtrl, ServerUtils utils) {
        super(mainCtrl, utils);
    }

    /**
     * Setup of question by setting the correct score, question and question number
     */

    public void init() {
        this.questionText.setText(question.getActivityList().get(0).title);
        this.questionImage.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(0).imagePath))));
        this.answerField.setText("");
        this.wrongInputText.setVisible(false);
        revealAnswerText.setVisible(false);

        eliminateJokerButton.setDisable(true);

        mainCtrl.setScene("Please enter your answer", SceneTypes.estimateSP);
    }

    /**
     * Logic when answer is submitted
     *
     * @param e event object passed automatically by JavaFX
     */
    @Override
    public void makeChoice(Event e) {
        // User clicks submit button
        if (timeBar.getProgress() >= 1.0d) {
            cancelTimer();
        }

        String input = answerField.getText();

        try {
            int answer = Integer.parseInt(input);
            updateScore(answer);
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid number submitted.");

            if (e != null) {
                // This will only run if a user clicked (which means the time is NOT over)
                // Thus method returns (so new timer does not start to show next question)
                wrongInputText.setVisible(true);
                return;
            }
        }

        setAnswersDisabled(true);

        // Only cancel timer if time runs out or valid answer is submitted
        cancelTimer();

        // A choice has been made (or time is up), and the user waits for next question to be shown
        mainCtrl.singleplayerGame.incrementQuestionsAnswered();

        revealAnswer("");

        mainCtrl.setStageTitle("Please wait for the next question");
        time.setText("Wait for next question...");
        startTimer(3.0, () -> {
            setAnswersDisabled(false); // Enable question answers again
            if (mainCtrl.singleplayerGame.lastQuestionReached()) {
                utils.addPlayerScore(mainCtrl.player);
                mainCtrl.showGlobalLeaderboard();
            } else {
                mainCtrl.showQuestion();
            }
        });
    }

    /**
     * Reveals the correct answer
     * @param buttonId id of the JavaFX object being pressed
     * @return returns true
     */
    public boolean revealAnswer(String buttonId) {
        // Show correct answer in input field
        revealAnswerText.setText("Close! Exact answer: " + question.getActivityList().get(0).consumptionInWh + " Wh");
        revealAnswerText.setVisible(true);
        return true;
    }

    /**
     * Enables/disables the submit button and input field
     *
     * @param isDisabled determines if buttons are disabled or not (true for disabled and false for not)
     */
    public void setAnswersDisabled(boolean isDisabled) {
        submitButton.setDisable(isDisabled);
        answerField.setDisable(isDisabled);
    }

    /**
     * Calculates how much the player was wrong
     *
     * @param guess the number given by the user
     * @return the calculated score
     */
    public int calculatePoints(int guess) {
        float target = question.getActivityList().get(0).consumptionInWh;
        float absDistance = Math.abs(target - guess);
        float percentage = absDistance / target;
        int points = 0;

        if (percentage < 0.1) {
            points = 1000;
        } else if (percentage < 0.2) {
            points = 800;
        } else if (percentage < 0.4) {
            points = 600;
        } else if (percentage < 0.6) {
            points = 400;
        } else if (percentage < 0.8) {
            points = 200;
        }

        return points;
    }

    public void eliminateAnswer() {

    }

}

