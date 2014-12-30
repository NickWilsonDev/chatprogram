/**
 * TailMessageLogger.java
 *
 * Class is a realization of the MessageLogger interface that keeps only the
 * last n log entries. That value is specified as a parameter to the 
 * constructor.
 *
 * @author Nick Wilson Chinou Yang
 * @version 11.6.13
 */

public class TailMessageLogger implements MessageLogger {

    /* An array of strings that are a recording of the last few messages */
    private String[] log;

    /* Field will tell how many elements are being used in the log array */
    private int inUse;

    /**
     * Constructor for this class.
     *
     * @param sizet - an int that will specify how large a group of messages
     *                should be logged
     */
    public TailMessageLogger(int sizet) {
        log = new String[sizet];
        inUse = 0;
    }

    /**
     * Method takes message provided in paramter and puts it into log array.
     * If a new message would be more than the log can contain, then the
     * oldest message in log is deleted and all other messages are moved back
     * in the log array.
     *
     * @param message - a string containing the message
     */
    public void log(String message) {
        if (inUse < log.length) {
            log[inUse] = message;
            inUse++;
        } else {
            for (int i = 0; i < log.length - 1; i++) {
                log[i] = log[i + 1];
            }
            log[log.length - 1] = message;
        }
    }

    /**
     * Method returns an array of messages(Strings) that serve as the logs for
     * this chat session.
     *
     * @return String[] - an array containing the logged entries for this chat
     *                    session
     */
    public String[] getLoggedEntries() {
        return log;
    }
}
