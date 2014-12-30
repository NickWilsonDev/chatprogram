import res.*;
import java.io.PrintStream;
import javax.swing.JTextArea;
/**
 * PrintStreamMessageListener.java
 *
 * Class is responsible for writing messages to a 
 * PrintStream(e.g., System.out). The class also implements the 
 * MessageListener interface, indicating that it plays the role of "observer"
 * in an instance of the observer pattern.
 *
 * @author Nick Wilson Chinou Yang
 * @version 11.6.13
 */

public class PrintStreamMessageListener implements MessageListener {

    /* A PrintStream that the message will be written to */
    private PrintStream out;

    /* An alternative output if the gui version is being used */
    private JTextArea convo;

    /**
     * Constructor for the class, it initializes the fields.
     *
     * @param out - a PrintStream
     */
    public PrintStreamMessageListener(PrintStream out) {
        this.out = out;
        convo = null;
    }

    /**
     * Constructor for the class, it initializes the fields.
     *
     * @param out - a PrintStream
     * @param convo - a JTextArea that provides an alternative output if the
     *                gui version is being used
     */
    public PrintStreamMessageListener(PrintStream out, JTextArea convo) {
        this.out = out;
        this.convo = convo;
    }

    /**
     * Prints to output stream (PrintStream). 
     *
     * @param message a String containing the message
     * @param source a MessageSource that can be used to notify any 
     *        "Observers"
     */
    public void messageReceived(String message, MessageSource source) {
        if (convo == null) {
            out.println(message);
        } else {
            convo.append(message + "\n");

        }
    }

    /**
     * Closes a specified MessageSource.
     *
     * @param source the MessageSource that will be closed
     */
    public void sourceClosed(MessageSource source) {
        source.removeMessageListener(this);
    }
}
