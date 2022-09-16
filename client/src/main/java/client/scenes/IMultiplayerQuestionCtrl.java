package client.scenes;

public interface IMultiplayerQuestionCtrl {

    /**
     * Removes time from the player; To be used by the joker system.
     *
     * @param percentage of time that is removed e.g. 0.6 - 60% of time is removed
     */
    public void removeTime(double percentage);

    /**
     * Method that clears the chat messages
     */
    public void clearChat();
}
