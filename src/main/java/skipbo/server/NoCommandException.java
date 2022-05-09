package skipbo.server;

/**
 * Exception thrown when a network protocol command is faulty.
 */
public class NoCommandException extends Exception {
    public String command;
    public String option;

    public NoCommandException() {}

    public NoCommandException(String command, String option) {
        this.command = command;
        this.option = option;
    }
}
