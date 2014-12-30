package res;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.lang.Thread;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * NetworkInterface.java
 *
 * Class is responsible for sending messages to and receiving messages from
 * remote hosts. The class extends the MessageSource class, indicating that it
 * can play the role of the "subject" in an instance of the observer pattern.
 * The class also implements the Runnable interface, indicating that it 
 * encapsulates the logic associated with the Thread.
 *
 * @author Nick Wilson Chinou Yang
 * @version 11.7.13
 */

public class NetworkInterface extends MessageSource implements Runnable{

    /* A socket that will provide a means of communication */
    private Socket socket;

    /* To read from the Socket */
    private BufferedReader in;

    /* Used to write from the Socket */
    private PrintStream    out;

    /* Used to enable multiple clients to connect at the same time */
    private Thread thread;

    /* Way of determining whether still connected or not */
    private boolean connected;

    /* Alternative output destination if gui client is used */
    private JTextArea conversation;

    /**
     * Constructor for the class, will initialize the fields.
     *
     * @param socket a Socket that will be used to provide input/output
     *               streams for communication
     *
     * @throws IOException if there is a problem accessing the socket's
     *                     streams, or starting a Thread
     */
    public NetworkInterface(Socket socket) throws IOException {
        this.socket = socket;
        try {
            in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            thread = new Thread(this);
            thread.start();
            connected = true;
            conversation = null;
        } catch (IOException io) {
            connected = false;
            throw io;
        }
    }

    /**
     * Alternative constructor for the class, will initialize the fields. It
     * is called if the gui client is being used.
     *
     * @param socket a Socket that will be used to provide input/output
     *               streams for communication
     * @param conversation a JTextArea that will provide an alternative 
     *                     destination for output
     *
     * @throws IOException if there is a problem accessing the socket's
     *                     streams, or starting a Thread
     */
    public NetworkInterface(Socket socket, JTextArea conversation) 
                        throws IOException {
        this.socket = socket;
        try {
            in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            thread = new Thread(this);
            thread.start();
            connected = true;
            this.conversation = conversation;
        } catch (IOException io) {
            connected = false;
            throw io;
        }
    }


    /**
     * Method sends a message to the target host, the client if the server is
     * sending and the server if the client is.
     *
     * @param message - String that contains the message that is to be sent.
     */
    public void sendMessage(String message) {
        if (conversation == null){
            out.println(" " + message);
        } else {
            out.println(" " + message);
            System.out.println("send Message in NetworkInterface is:: " 
                                + message);
            //conversation.append(message + "\n");
        }
    }

    /**
     * Method provides a way to test whether still connected or not, it tries
     * to read from the socket and returns false if unsuccessful, true 
     * otherwise.
     *
     * @return true if still connected, false if not
     */
    public boolean isConnected() {
        try {
            if (in.read() == -1) {
                return false;
            }
        } catch (IOException connect) {
            return false;
        }
        return true;
    }

    /**
     * Method attempts to close all aspects of the NetworkInterface. If an
     * exception is caught is ignored since it would probably mean that the
     * socket, stream, or thread was already closed or didnt exist.
     */
    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
            thread.interrupt();
        } catch (IOException close) {
            
        } finally {
            connected = false;
            
        }
    }

    /**
     * Method is required to implement Runnable interface, it provides the
     * code that will be run each time a Thread is started. Exceptions caught
     * will be ignored other than printing out an error message since in order
     * to implement Runnable the run method cannot throw an exception.
     */
    public void run() {
        while (isConnected()) {
            try {
                String str = in.readLine();
                this.notifyReceipt(str);
            } catch (IOException run) {
                System.out.println("Problem in run() in Networkinterface");
            }
        }
    }
}
