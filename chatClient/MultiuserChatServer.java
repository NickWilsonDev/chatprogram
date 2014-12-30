import res.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * MultiuserChatServer.java
 *
 * MultiuserChatServer is one of the classes that implement the server-side
 * logic of this client-server application. It is responsible for accepting
 * incoming connections, creating NetworkInterfaces, and passing the 
 * NetworkInterface off to threads for processing. The class implements the
 * MessageListener interface (i.e., it can "observe" objects that are
 * MessageSources).
 *
 * @author Nick Wilson and Chinou Yang
 * @version 11.22.13
 */

public class MultiuserChatServer implements MessageListener {

    /* A ServerSocket that listens for incoming connections */
    private ServerSocket serverSocket;

    /* A NetworkInterface that is created each time a connection is made */
    private NetworkInterface netInt;

    /* A logger that will contain all messages sent by all clients */
    private DateDecoratorMessageLogger logger;

    /* An int that specifies how many past messages to keep in the log */
    private int size = 5;

    /* A Socket for communicating with the client */
    private Socket sockClient;

    /* Map MessageSources to usernames */
    private HashMap<MessageSource, String> msHashMap;

    /* Map users to NetworkInterfaces */
    private HashMap<String, NetworkInterface> niHashMap;

    /* String containing user's name */
    private String key;

    /* Boolean for determining whether connection has been closed or not */
    private boolean connection;

    /**
     * Constructor for this class, it initializes the fields.
     *
     * @param port and int containing the port number
     *
     * @throws IOException if there is a problem creating a HashMap, 
     *                     ServerSocket, or DateDecoratorMessageLogger
     */
    public MultiuserChatServer(int port) throws IOException{
        try {
            connection = true;
            netInt     = null;
            msHashMap  = new HashMap<MessageSource, String>();
            niHashMap  = new HashMap<String, NetworkInterface>();
            serverSocket = new ServerSocket(port);
            logger = new DateDecoratorMessageLogger(new TailMessageLogger(size));
        } catch (IOException server) {
            throw server;
        }
    }

    /**
     * Method listens for incoming connections from clients. When a connection
     * is made it creates a NetworkInterface to communicate with and also adds
     * this to the list of MessageListeners. Then it starts a new thread for
     * communication to be made through.
     *
     * @throws IOException if there is a problem creating a NetworkInterface,
     *                     Thread, or adding a MessageListener
     */
    public void listen() throws IOException {
        try {
            netInt = new NetworkInterface(serverSocket.accept());
            this.netInt.addMessageListener(this);
            new Thread(netInt).start();
            System.out.println("User connected");

        } catch (IOException listen) {
            throw listen;
        }
    }

    /**
     * Method sends a message to every NetworkInterface that is connected to
     * the server.
     *
     * @param message a String containing the message
     */
    public void broadcast(String message) {
        logger.log(message);
        for (Entry<String, NetworkInterface> entry : niHashMap.entrySet()) {
            NetworkInterface ni = entry.getValue();
            ni.sendMessage(message);
        }
    }

    /**
     * Method parses the sent message, checking for commands and determines
     * what to do and what clients to send the message to.
     *
     * @param message a String containing the message
     * @param source a MessageSource that the message was sent from
     */
    public void messageReceived(String message, MessageSource source) {
        System.out.println("inside messageReceived message::");
        System.out.println(message);
        Scanner scan;
        String str = "";
        scan = new Scanner(message);

        while (scan.hasNextLine()) {
            key = msHashMap.get(source);
            str = scan.next();
            if (str.equals("/hello")) {
                helloCommand(scan, str);
            } else if (str.equals("/who")) {
                whoCommand(key);
            } else if (str.equals("/msg")) {
                if (scan.hasNext()) {
                    str = scan.next();
                    NetworkInterface ni = niHashMap.get(str.trim());
                    if (scan.hasNextLine()) {
                        str = scan.nextLine();
                        ni.sendMessage("(private) <" + key + ">:" + str);
                    } else {
                        System.out.println("msg body not included");
                    }
                }
            }  else if (str.equals("/red")) {
                redCommand(scan, str);
            } else if (str.equals("/quit")) {
                this.broadcast(" !!! " + key + " disconnected");
                sourceClosed(source);
            } else {
                if (scan.hasNextLine()) {
                    scan.nextLine();
                }
                this.broadcast("<" + key + ">: " + message);
            }
        }
    }

    /**
     * A helper method that was called from the messageReceived method if the
     * command "/hello" was found. It adds a new username and NetworkInterface
     * to the HashMap, displays whatever is currently stored in the log, and
     * notifies all currently connected clients the this new user has
     * connected.
     *
     * @param scan a Scanner that is used to parse the rest of the string
     * @param str a String that should contain the user's name
     */
    private void helloCommand(Scanner scan, String str) {
        if (scan.hasNext()) {
            str = scan.next();
            niHashMap.put(str, netInt);
            msHashMap.put(netInt, str);
            String[] array = this.logger.getLoggedEntries();
            for (int i = 0; i < array.length; i++) {
                if (array[i] != null) {
                    netInt.sendMessage(array[i]);
                }
            }
            this.broadcast("!!! " + str + " has joined");
        } else {
            this.netInt.close();
        }
    }

    /**
     * A helper method that changes the text display to the red color.
     *
     * @param scan a Scanner that is used to parse the rest of the string
     * @param str a String that should contain the user's name
     */
    private void redCommand(Scanner scan, String str) {
        if (scan.hasNext()) {
            str = scan.nextLine();
            System.out.println("red message ::" + str);
            this.broadcast("<" + key + ">: " + "\033[91m"
                               + str + "\033[0m");
            }
    }

    /**
     * A helper method that displays the currently connected users.
     *
     * @param key a String that contains the user's name
     */
    private void whoCommand(String key) {
        NetworkInterface ni = niHashMap.get(key);
        for (Entry<String,
            NetworkInterface> entry : niHashMap.entrySet()) {
            ni.sendMessage("---> " + entry.getKey());
        }
    }

    /**
     * Method closes specified MessageSource and removes the corresponding
     * entries from the HashMaps, that are used to store the list of users.
     *
     * @param source a MessageSource that will be closed and removed
     */
    public void sourceClosed(MessageSource source) {
        niHashMap.get(msHashMap.get(source)).removeMessageListener(this);
        niHashMap.get(msHashMap.get(source)).close();
        niHashMap.remove(msHashMap.get(source));
        msHashMap.remove(source);
        if (msHashMap.isEmpty()) {
            connection = false;
        }
    }

    /**
     * Method simply returns true if connected and false if not.
     * 
     * @return connection, a boolean that istrue if connected and false if not
     */
    public boolean isConnected() {
        return connection;
    }
}
