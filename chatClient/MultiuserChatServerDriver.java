import java.io.IOException;

/**
 * MultiuserChatServerDriver.java
 *
 * This class contains the main() method for the server. It parses command
 * line options, instantiates a MultiuserChatServer and calls its 
 * listen() method.
 *
 * @author Nick Wilson Chinou Yang
 * @version 11.6.13
 */

public class MultiuserChatServerDriver {

    /**
     * Method is the entry point of the program.
     *
     * @param args array containing the command line arguments
     *        arg[0] should contain the port number that the server will
     *               listen for connections on
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("usage is: java MultiuserChatServerDriver "
                    + "<port#>");
            System.exit(0);
        }

        int port = Integer.parseInt(args[0]); 
        try {
            MultiuserChatServer server = new MultiuserChatServer(port);

            while (server.isConnected()) {
                server.listen();
            }
        } catch (IOException io) {
            System.out.println("couldnt initialize server");
            System.exit(0);
        }
    }
}
