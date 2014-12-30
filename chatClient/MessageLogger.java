/**
 * MessageLogger interface provides an interface for message loggers, so they
 * all follow the same structure.
 *
 * @author Nick Wilson
 * @version 11.6.13
 */

public interface MessageLogger {

    /**
     * Adds a new message to the array of messages.
     *
     * @param message a String containing the message
     */
    public void log(String message);

    /**
     * Returns the array of messages.
     *
     * @return a String[] containing the messages.
     */
    public String[] getLoggedEntries();
}
