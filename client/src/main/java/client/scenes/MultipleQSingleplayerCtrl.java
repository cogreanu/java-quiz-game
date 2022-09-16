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

public class MultipleQSingleplayerCtrl extends IQuestionCtrl {

    @FXML
    ImageView image;
    @FXML
    Button choice1Button;
    @FXML
    Button choice2Button;
    @FXML
    Button choice3Button;
    @FXML
    Text questionText;

    public int guaranteedPoints= 500;

    /**
     * Constructor for MultipleQSingleplayerCtrl
     *
     * @param mainCtrl the MainCtrl to be used
     * @param utils    serverutils object
     */
    @Inject
    public MultipleQSingleplayerCtrl(MainCtrl mainCtrl, ServerUtils utils) {
        super(mainCtrl, utils);
    }

    public void init() {
        // restores initial state of elements
        choice1Button.setStyle("-fx-fill: black");
        choice2Button.setStyle("-fx-fill: black");
        choice3Button.setStyle("-fx-fill: black");

        List<Long> answers = generateRandomAnswers(question.getActivityList().get(0).consumptionInWh);

        choice1Button.setText(answers.get(0).toString());
        choice2Button.setText(answers.get(1).toString());
        choice3Button.setText(answers.get(2).toString());

        // initialize fields
        image.setImage(new Image(new ByteArrayInputStream(utils.getImage(question.getActivityList().get(0).imagePath))));

        questionText.setText(question.getActivityList().get(0).title);

        mainCtrl.setScene("Please enter your answer", SceneTypes.multipleSP);
    }

    /**
     * generates random answers close to the game answer
     * @param actualAnswer answer to be close to
     * @return returns a list of the answers with 2 random wrong ones and 1 correct one placed randomly in the list
     */
    public List<Long> generateRandomAnswers(Long actualAnswer) {
        Random rand = new Random();

        List<Long> answers = new ArrayList<>();

        long wrongAnswer1;
        long wrongAnswer2;

        System.out.println("Generating random answers.");

        switch (rand.nextInt(3)) {
            case 0:
                // Correct answer will be lower
                wrongAnswer1 = (long) (actualAnswer + actualAnswer * randomPercentage(rand));
                do {
                    wrongAnswer2 = (long) (actualAnswer + actualAnswer * randomPercentage(rand));
                } while (wrongAnswer1 == wrongAnswer2);
                answers.add(actualAnswer);
                answers.add(wrongAnswer1);
                answers.add(wrongAnswer2);
                break;
            case 1:
                // Correct answer will be in the middle
                wrongAnswer1 = (long) (actualAnswer - actualAnswer * randomPercentage(rand));
                do {
                    wrongAnswer2 = (long) (actualAnswer + actualAnswer * randomPercentage(rand));
                } while (wrongAnswer1 == wrongAnswer2);
                answers.add(wrongAnswer1);
                answers.add(actualAnswer);
                answers.add(wrongAnswer2);
                break;
            case 2:
                // Correct answer will be higher
                wrongAnswer1 = (long) (actualAnswer - actualAnswer * randomPercentage(rand));
                do {
                    wrongAnswer2 = (long) (actualAnswer - actualAnswer * randomPercentage(rand));
                } while (wrongAnswer1 == wrongAnswer2);
                answers.add(wrongAnswer1);
                answers.add(wrongAnswer2);
                answers.add(actualAnswer);
                break;
            default:
                System.out.println("Error generating questions");
        }

        System.out.println("DONE Generating random answers.");

        return answers;
    }

    /**
     * generates a random percentage
     * @param rand which random class to use
     * @return the randomly generated percentage
     */
    public double randomPercentage(Random rand) {
        int maxPercentage = 4; // Max spread of the answer (multiply by 10). E.g. 4 -> 40% max spread
        // useful for easy change of spread
        return (rand.nextInt(maxPercentage) + 1) / 10.0;
    }

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
     * Reveals the correct answer by coloring it green and the rest red
     * @param buttonId id of the JavaFX object being pressed
     * @return whether the button that was pressed was the correct answer
     */
    @Override
    public boolean revealAnswer(String buttonId) {
        // Disable all answers
        setAnswersDisabled(true);

        Long consumption = question.getActivityList().get(0).consumptionInWh;

        // initially colors everything red
        choice1Button.setStyle("-fx-background-color: firebrick");
        choice2Button.setStyle("-fx-background-color: firebrick");
        choice3Button.setStyle("-fx-background-color: firebrick");

        boolean correct = false;

        if (choice1Button.getText().equals(consumption.toString())) {
            choice1Button.setStyle("-fx-background-color: green");
            if (buttonId.equals(choice1Button.getId())) {
                correct = true;
            }
        } else if (choice2Button.getText().equals(consumption.toString())) {
            choice2Button.setStyle("-fx-background-color: green");
            if (buttonId.equals(choice2Button.getId())) {
                correct = true;
            }
        } else if (choice3Button.getText().equals(consumption.toString())) {
            choice3Button.setStyle("-fx-background-color: green");
            if (buttonId.equals(choice3Button.getId())) {
                correct = true;
            }
        } else {
            System.out.println("No buttons matching... Something's broken");
        }

        return correct;
    }

    /**
     * Disables the ability to answer
     * @param isDisabled determines if the buttons should be disabled or not (true for disabled and false for not)
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

        Long correctAnswer = question.getActivityList().get(0).consumptionInWh;

        buttons = buttons.stream().filter(b -> !b.getText().equals(correctAnswer.toString())).collect(Collectors.toList());

        int i = new Random().nextInt(buttons.size()); // incase buttons have duplicate answers and more
                                                    // than 1 is removed, this will always only remove the wrong one

        buttons.get(i).setStyle("-fx-background-color: firebrick");
        buttons.get(i).setDisable(true);
    }
}
