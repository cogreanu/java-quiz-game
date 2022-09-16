package client.scenes;

import client.utils.ServerUtils;
import commons.Question;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

public abstract class IQuestionCtrl {

    @FXML
    Text time;
    @FXML
    ProgressBar timeBar;
    @FXML
    Text score;
    @FXML
    Text questionNo;
    @FXML
    Button doubleJokerButton;
    @FXML
    Button eliminateJokerButton;
    @FXML
    Text addedScore;
    @FXML
    Button exitButton;

    MainCtrl mainCtrl;
    ServerUtils utils;

    Timer gameTimer;
    Question question;
    boolean doublePoints;

    @Inject
    public IQuestionCtrl(MainCtrl mainCtrl, ServerUtils utils) {
        this.mainCtrl = mainCtrl;
        this.utils = utils;
    }

    /**
     * Initializes the {@code Controller} with a {@code Question}.
     * This method is called for all question types to initialize common behaviour.
     * The initialize method handles question-specific initialization.
     * @param question used to initialize the {@code Controller}.
     */
    public void generalInitialize(Question question) {
        // Ensure answers are enabled (in case user quits unexpectedly for the previous game)
        setAnswersDisabled(false);

        // Reset score text
        this.score.setText("Score: " + mainCtrl.player.score);

        // restore Text 'time's initial state
        time.setText("Time left:");

        // Set number of questions completed counter
        this.questionNo.setText((mainCtrl.singleplayerGame.getNumOfQuestionsAnswered() + 1) + "/" + mainCtrl.singleplayerGame.totalQuestions);

        doubleJokerButton.setDisable(mainCtrl.player.doubleJoker);
        doublePoints = false;
        addedScore.setVisible(false);

        eliminateJokerButton.setDisable(mainCtrl.player.answerJoker);

        this.question = question;

        init(); // specific initialization for each question

        startTimer(10.0, () -> makeChoice(null));
    }

    abstract void init();

    /**
     * Starts the timer for either the answer or rest phase of the game
     * <p>
     * This is reflected in the progress bar
     *
     * @param time     amount of time available to the player
     * @param function what should happen after the time runs out e.g. show answers / next question
     *                 <p><strong>NOTE:</strong> not to be confused with the {@link Runnable} used in threads!
     *                 It is used as a general functional interface.
     */
    void startTimer(double time, Runnable function) {
        timeBar.setProgress(0.0d);

        double period = 0.01d / time;

        cancelTimer();
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                double progress = timeBar.getProgress();
                progress += period;
                if (progress >= 1.0d) {
                    // prevents rounding errors that show the bar as incomplete
                    timeBar.setProgress(1.0d);
                    // stops timer from running indefinitely
                    gameTimer.cancel();
                    // Platform.runLater() is needed to avoid thread errors when within a timer
                    Platform.runLater(function);
                } else {
                    timeBar.setProgress(progress);
                }
            }
        }, 0, 10);
    }

    /**
     * To be used by the question buttons in the scene.
     * <p>Dictates what happens when the player selects an option.
     *
     * @param e event object passed automatically by JavaFX
     */
    abstract void makeChoice(Event e);

    /**
     * Reveals the correct answer to the player.
     *
     * @param buttonId id of the JavaFX object being pressed
     * @return true if correct answer was pressed, false if not
     */
    abstract boolean revealAnswer(String buttonId);

    /**
     * Sets all answer buttons to either disabled or enabled
     *
     * @param isDisabled determines if buttons are disabled or not (true for disabled and false for not)
     */
    abstract void setAnswersDisabled(boolean isDisabled);

    /**
     * Updates the score of the player
     */
    public void updateScore(int guess) {
        int pointsToAdd = 0;
        switch (question.getType()) {
            case estimate:
                float target = question.getActivityList().get(0).consumptionInWh;
                float absDistance = Math.abs(target - guess);
                float percentage = absDistance / target;

                if (percentage < 0.05) {
                    pointsToAdd = 1000;
                } else if (percentage < 0.1) {
                    pointsToAdd = 800;
                } else if (percentage < 0.2) {
                    pointsToAdd = 600;
                } else if (percentage < 0.4) {
                    pointsToAdd = 400;
                } else if (percentage < 0.6) {
                    pointsToAdd = 200;
                }
                break;
            default: // for mostExp, alternative and multiple
                pointsToAdd = (int) (Math.max(1000 * (1 - timeBar.getProgress()), 500));
                break;
        }
        
        pointsToAdd = (int) (Math.round(pointsToAdd / 10.0) * 10);

        if(doublePoints) {
            pointsToAdd *= 2;
        }

        addedScore.setText("+ " + pointsToAdd);
        addedScore.setVisible(true);
        mainCtrl.player.addScore(pointsToAdd);
        score.setText("Score: " + mainCtrl.player.score);
    }

    /**
     * Returns the player to the Home screen
     */
    public void goBack() {
        // Cancel default 20s countdown timer (to not reveal answers twice)
        cancelTimer();

        // Reset score for future games
        mainCtrl.player.score = 0;
        mainCtrl.player.useDoubleJoker(false);

        mainCtrl.showHome();
    }

    /**
     * Gives player double points
     */
    public void doublePoints() {
        doublePoints = true;
        mainCtrl.player.useDoubleJoker();
        doubleJokerButton.setDisable(true);
    }

    abstract void eliminateAnswer();

    public void cancelTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }
}
