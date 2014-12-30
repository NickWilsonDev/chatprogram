import java.io.IOException;
import java.util.Scanner;

/**
 * MultiuserChatClientDriver.java
 *
 * This file contains the main() method for the client. It parses the command
 * line options instantiates a MultiuserChatClient, reads messages from the
 * keyboards, and sends them to the client.
 *
 * @author Nick Wilson Chinou Yang
 * @version 11.6.13
 */

public class MultiuserChatClientDriver {

    /**
     * The entry point of the program it parses the command line arguments,
     * creates a MultiuserChatClient, and handles user keyboard input.
     *
     * @param args a String[] defining the server and user names, and the port
     *        number
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("usage is: java MultiuserChatClientDriver " +
                                "<server> <port#> <username>");
            System.exit(0);
        }

        String hostname = args[0];
        int port        = Integer.parseInt(args[1]);
        String userName = args[2];
        MultiuserChatClient client;
        try {
            client = new MultiuserChatClient(hostname, 
                        port, userName);
            client.connect();
            Scanner input = new Scanner(System.in);
            boolean disconnect = false;
            String msg = "";

            while (disconnect != true) {
                msg = input.nextLine();
                client.send(" " + msg);
                if (msg.equals("/quit")) {
                    disconnect = true;
                    System.exit(0);
                }
            }
        } catch (IOException io) {
            System.out.println("Problem creating ChatClient");
            System.exit(0);
        }
    }
}
