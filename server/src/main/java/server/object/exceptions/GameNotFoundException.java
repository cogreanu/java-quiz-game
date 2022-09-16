package server.object.exceptions;

/**
 * Exception that is thrown when a {@link server.object.MultiplayerGame} with a given ID doesn't exist
 */

public class GameNotFoundException extends Exception {
    public GameNotFoundException(String message) {
        super(message);
    }
}
