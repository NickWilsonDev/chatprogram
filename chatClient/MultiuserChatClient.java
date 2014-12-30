import res.*;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.NullPointerException;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.net.UnknownHostException;

/**
 * MultiuserChatClient.java
 *
 * Class is of one of several classes that implement the client-side logic of
 * the client-server application. It is responsible for creating a
 * NetworkInterface, reading input from the user, and sending that input to 
 * the server via the NetworkInterface. The class implements the 
 * MessageListener interface(i.e. it can "observe" objects that are 
 * MessageSources). The class also extends MessageSource, indicating that it
 * also plays the role of "subject" in an instance of the observer pattern.
 *
 * @author Nick Wilson
 * @version 11.7.13
 */

public class MultiuserChatClient extends MessageSource
                implements MessageListener {

    /* Field will contain address of server */
    private InetAddress host;

    /* Field will contain port number that will be used */
    private int port;

    /* Field contains users nickname */
    private String nickname;

    /* Field for socket, used in NetworkInterface */
    private Socket socket;

    /* Field is a NetworkInterface, used to connect and send messages */
    private NetworkInterface netInt;

    /* Will provide an alternative output destination if gui client is used */
    private JTextArea conversation;

    /**
     * Constructor for the class, it initializes all the fields
     * 
     * @param hostname - String that contains server name we will connect to
     *        port - int that tells what port we will connect to
     *        nickname - String that contains users nickname
     *
     * @throws IOException if there is a problem creating a socket
     *
     */
    public MultiuserChatClient(String hostname, int port, String nickname) 
                        throws UnknownHostException, IOException {
        try {
            this.host     = InetAddress.getByName(hostname);
        } catch(UnknownHostException noHost) {
            throw noHost;
        }
        this.port     = port;
        this.nickname = nickname;
        this.netInt   = null;
        this.conversation = null;

        try {
            this.socket   = new Socket(host, this.port);
        } catch (IOException sock) {
            throw sock;
        }
    }

     /**
     * Constructor for the class, it initializes all the fields. This
     * constructor is specifically used by the the gui client.
     * 
     * @param hostname - String that contains server name we will connect to
     * @param port - int that tells what port we will connect to
     * @param nickname - String that contains users nickname
     * @param conversation - a JTextArea that gives place to send output
     *
     * @throws IOException if there is a problem creating a socket
     *
     */
    public MultiuserChatClient(String hostname, int port, String nickname, 
            JTextArea conversation) throws UnknownHostException, 
            IOException {
        try {
            this.host     = InetAddress.getByName(hostname);
        } catch(UnknownHostException noHost) {
            throw noHost;
        }
        this.port     = port;
        this.nickname = nickname;
        this.netInt   = null;
        this.conversation = conversation;

        try {
            this.socket   = new Socket(host, this.port);
        } catch (IOException sock) {
            throw sock;
        }
    }

    /**
     * Method connects to server via a NetworkInterface. This method is also
     * a forking point for the gui client.
     *
     * @throws NullPointerException if nothing was passed for the nickname in
     *                              in the constructor.
     * @throws IOException if there was a problem creating a NetworkInterface
     */
    public void connect() throws NullPointerException, IOException {
        try {
            if (conversation == null) {
                netInt = new NetworkInterface(socket);
            } else {
                netInt = new NetworkInterface(socket, conversation);
            }
            netInt.addMessageListener(this);
            PrintStreamMessageListener output;
            if (conversation == null) {
                output = new PrintStreamMessageListener(new 
                        PrintStream(System.out));
            } else {
                output = new PrintStreamMessageListener(new 
                        PrintStream(System.out), conversation);
            }
            this.addMessageListener(output);
            this.send(" /hello " + this.nickname);
        } catch (NullPointerException connect) {
            throw connect;
        } catch (IOException ioCon) {
            throw ioCon;
        }
    }

    /**
     * Method sends a message to the server via the NetworkInterface.
     *
     * @param message - a String that contains the message
     */
    public void send(String message) {
        this.netInt.sendMessage(message);
    }

    /**
     * Method notifies specified MessageSource that this String was recieved.
     *
     * @param message a String containing the message
     * @param source, the MessageSource that will be notified
     */
    public void messageReceived(String message, MessageSource source) {
        notifyReceipt(message);
    }

    /**
     * Method removes this chatclient from the specified MessageSource.
     *
     * @param source a MessageSource that will remove this MessageListener
     *        from itself
     */
    public void sourceClosed(MessageSource source) {
        source.removeMessageListener(this);
    }
}
