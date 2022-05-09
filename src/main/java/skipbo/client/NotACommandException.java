package skipbo.client;

/**
 * Exception thrown when a command from the client is faulty.
 */
public class NotACommandException extends Exception {

    public NotACommandException() {
    }

    public NotACommandException(String message) {
        super(message);
    }
}
