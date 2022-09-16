package client;

import commons.Activity;

import java.util.List;

public class SingleplayerGame {

    private int numOfQuestionsAnswered;
    private List<Activity> activities;

    public static int totalQuestions = 20;

    public SingleplayerGame() {
    }

    public SingleplayerGame(List<Activity> activities) {
        this.activities = activities;
    }

    public int getNumOfQuestionsAnswered() {
        return numOfQuestionsAnswered;
    }

    public void incrementQuestionsAnswered() {
        this.numOfQuestionsAnswered++;
    }

    public boolean lastQuestionReached() {
        return this.numOfQuestionsAnswered >= totalQuestions;
    }
}
