import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateDecoratorMessageLogger.java
 * Class is a realization of the MessageLogger interface. This class
 * represents an instance of the decorator design pattern. Instances of the 
 * class "wrap" other realizations of the MessageLogger interface, and append
 * the current date and time to any logged messages.
 * 
 * @author Nick Wilson Chinou Yang
 * @version 11.6.13
 */

public class DateDecoratorMessageLogger implements MessageLogger {

    /* String array that will contain the log for the chat session */
    private String[] decoLog;

    /* Int tells how many slots in array are being used */
    private int inUse;

    /**
     * Constructor for the class, it will initialize all fields.
     *
     * @param logger - MessageLogger
     */
    public DateDecoratorMessageLogger(MessageLogger logger) {
        decoLog = logger.getLoggedEntries();
        inUse = 0;
        for (int i = 0; i < decoLog.length; i++) {
            if (decoLog[i] != null)
                inUse++;
        }
    }

    /**
     * Method puts a message, provided in the parameter, into an array of 
     * Strings. But first it gets the system's date and time and prepends
     * this data to the message.
     *
     * @param message - String containing message
     */
    public void log(String message) {
        // set format and get date and time from system
        String format = "EEE MMM dd HH:mm:ss zzz yyyy:";
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        String time = dateFormat.format(date) + " ";
        String newMessage = time + message;

        // put the new message in the log
        if (inUse < decoLog.length - 1) {
            decoLog[inUse + 1] = newMessage;
            inUse++;
        } else {
            String[] temp = new String[decoLog.length * 2];
            System.arraycopy(decoLog, 0, temp, 0, decoLog.length);
            decoLog = temp;
            decoLog[inUse + 1] = newMessage;
            inUse++;
        }
    }

    /**
     * Method returns the log(String[]) of messages.
     *
     * @return String[] - the log of messages
     */
    public String[] getLoggedEntries() {
        return decoLog;
    }
}
