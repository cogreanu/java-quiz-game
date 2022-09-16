package server.object.exceptions;

/**
 * Exception that is thrown if the {@link commons.Player} can't be found in the
 * {@link server.object.MultiplayerGame}'s player Map
 */

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
