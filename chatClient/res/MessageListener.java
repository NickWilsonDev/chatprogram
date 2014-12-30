package res;
/**
 * MessageListener.java interface
 *
 * File defines the interface to objects that can "observe" other objects that
 * receive messages. When the subject receives a message, the message is
 * forwarded to all registered observers.
 *
 * @author Nick Wilson Chinou Yang
 * @version 11.6.13
 */

public interface MessageListener {

    /**
     * Will provide a way of notifying to the observers that a message was
     * recieved.
     *
     * @param message a String containing the message
     * @param source a MessageSource that tells where the message comes from
     */
    public void messageReceived(String message, MessageSource source);

    /**
     * Will provide a way of closing the specified MessageSource.
     *
     * @param source a MessageSource that will be closed
     */
    public void sourceClosed(MessageSource source);
}
