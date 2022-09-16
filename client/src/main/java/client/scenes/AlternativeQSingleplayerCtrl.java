package client.scenes;

import client.utils.ServerUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AlternativeQSingleplayerCtrl extends IQuestionCtrl {
    String correctButtonId;

    @FXML
    ImageView image;

    @FXML Text questionText;
    @FXML Button choice1Button;
    @FXML Button choice2Button;
    @FXML Button choice3Button;

    @Inject
    public AlternativeQSingleplayerCtrl(MainCtrl mainCtrl, ServerUtils utils) {
        super(mainCtrl, utils);
    }

    /**
     * Setup of question by setting the correct score, question and question number
     */
    public void init() {
        this.questionText.setText("What could you do instead of '" + question.getActivityList().get(0).title + "'?");

        // restores initial state of elements
        choice1Button.setStyle("-fx-fill: black");
        choice2Button.setStyle("-fx-fill: black");
        choice3Button.setStyle("-fx-fill: black");
        this.image.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(0).imagePath))));
        setButtons(new Random());

        mainCtrl.setScene("Please enter your answer", SceneTypes.alternativeSP);
    }

    /**
     * Puts a '\n' character (new line character) instead of a space as close as possible to index 40.
     *
     * @param s string for which to perform the operation
     * @return string that holds the '\n' at around index 40 of the string
     */
    public String splitText(String s) {
        int index = 0;
        int lastIndex = 0;
        while (index != -1) {
            index = s.indexOf(" ", index + 1);
            if (index > 40 && lastIndex != 0) {
                return s.substring(0, lastIndex) + "\n" + s.substring(lastIndex + 1);
            }
            lastIndex = index;
        }
        return s;
    }

    /**
     * randomly assigns the activity titles to each button and sets
     * which button is correct
     *
     * @param random used for randomly assigning which button is correct
     */
    public void setButtons(Random random) {
        String[] titles = new String[]{
                question.getActivityList().get(1).title,
                question.getActivityList().get(2).title,
                question.getActivityList().get(3).title
        };

        for (int i = 0; i < 3; i++) {
            if (titles[i].length() > 40) {
                titles[i] = splitText(titles[i]);
            }
        }
        switch (random.nextInt(3)) {
            case 0 -> {
                correctButtonId = "choice1Button";
                choice1Button.setText(titles[0]);
                choice2Button.setText(titles[1]);
                choice3Button.setText(titles[2]);
            }
            case 1 -> {
                correctButtonId = "choice2Button";
                choice1Button.setText(titles[1]);
                choice2Button.setText(titles[0]);
                choice3Button.setText(titles[2]);
            }
            default -> {
                correctButtonId = "choice3Button";
                choice1Button.setText(titles[1]);
                choice2Button.setText(titles[2]);
                choice3Button.setText(titles[0]);
            }
        }
    }

    /**
     * Logic when answer is submitted
     *
     * @param e event object passed automatically by JavaFX
     */
    public void makeChoice(Event e) {
        // User clicks submit button
        cancelTimer();

        // Increment number of questions answered
        mainCtrl.singleplayerGame.incrementQuestionsAnswered();

        String inputButton;
        if (e != null) {
            inputButton = ((Node) e.getSource()).getId();
        } else {
            inputButton = "";
        }

        if (revealAnswer(inputButton)) {
            updateScore(1000);
        }

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
     * Reveals the answer to the player by coloring the buttons
     * @param buttonId id of the JavaFX object being pressed
     * @return returns whether the player is correct
     */
    public boolean revealAnswer(String buttonId) {
        // Disable all answers
        setAnswersDisabled(true);

        // initially colors everything red
        choice1Button.setStyle("-fx-background-color: firebrick");
        choice2Button.setStyle("-fx-background-color: firebrick");
        choice3Button.setStyle("-fx-background-color: firebrick");

        // then colors the right answer green
        if (choice1Button.getId().equals(correctButtonId)) {
            choice1Button.setStyle("-fx-background-color: green");
        } else if (choice2Button.getId().equals(correctButtonId)) {
            choice2Button.setStyle("-fx-background-color: green");
        } else {
            choice3Button.setStyle("-fx-background-color: green");
        }

        return buttonId.equals(correctButtonId);
    }

    /**
     * Enables/disables the submit button and input field
     *
     * @param isDisabled determines if buttons are disabled or not (true for disabled and false for not)
     */
    public void setAnswersDisabled(boolean isDisabled) {
        choice1Button.setDisable(isDisabled);
        choice2Button.setDisable(isDisabled);
        choice3Button.setDisable(isDisabled);
    }

    /**
     * Shows which answer is definitely wrong to the player by coloring that answer red
     */
    public void eliminateAnswer() {
        mainCtrl.player.useAnswerJoker();
        eliminateJokerButton.setDisable(true);

        // then colors the right answer green
        List<Button> buttons = new ArrayList<>();
        buttons.add(choice1Button);
        buttons.add(choice2Button);
        buttons.add(choice3Button);

        buttons = buttons.stream().filter(b -> !b.getId().equals(correctButtonId)).collect(Collectors.toList());

        int i = new Random().nextInt(2);

        buttons.get(i).setStyle("-fx-background-color: firebrick");
        buttons.get(i).setDisable(true);
    }
}
