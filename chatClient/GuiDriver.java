/**
 * GuiDriver.java
 *
 * This class contains the main() method for the client. It parses the command
 * line options instantiates a MultiuserChatClient, reads messages from the
 * keyboards, and sends them to the client.
 *
 * @author Nick Wilson Chinou Yang
 * @version 11.6.13
 */

import res.*;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.IOException;
import java.net.UnknownHostException;

// swing stuff
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GuiDriver extends MessageSource implements MessageListener {

    /* String to hold hostname */
    private String hostname;

    /* Int to hold port number */
    private int port;

    /* String to hold nick name */
    private String userName;

    /* Initial main window for getting data from user for connection */
    private JFrame setupConnection;

    /* JPanel to organize layout */
    private JPanel setupPanel;

    /* Field for user to input hostname */
    private JTextField hostName;

    /* Field for user to input port number */
    private JTextField portNum;

    /* Field for user to input nick name */
    private JTextField nickName;

    /* Button to make actual connection */
    private JButton connectButton;

    /* Main chat frame */
    private JFrame chatFrame;

    /* JPanel to organize layout */
    private JPanel chatPanel;

    /* Area where the messages are written to */
    private JTextArea conversation;

    /* Field for user to put message */
    private JTextField userInput;

    /* Button to send message */
    private JButton sendButton;

    /* Button to disconnect client */
    private JButton disconnectButton;

    /* Printstream redirect standard out to */
    private PrintStream output;

    /**
     * The constructor for the class, it initializes the fields and begins
     * creating the graphical interface for getting connection options from
     * the user.
     */
    public GuiDriver() {
        setupConnection = new JFrame();
        ActionListener buttonListener = null;
        JPanel tempPanel = null; // this will be used to setup the rows
        setupPanel = new JPanel(new GridLayout(4, 1));
        setupPanel.add(hostLayout(tempPanel));
        setupPanel.add(portLayout(tempPanel));
        setupPanel.add(nickLayout(tempPanel));
        tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupConnection.setVisible(false);
                final ClientWrap wClient;
                conversation = new JTextArea(10, 20);
                try {
                    MultiuserChatClient client = new MultiuserChatClient(
                            hostname, port, userName, conversation);
                    client.connect();
                    wClient = new ClientWrap(client);
                    guiChat(wClient);
                } catch (UnknownHostException unHost) {
                    System.exit(0);
                } catch (IOException io) {
                    System.exit(0);
                }
            }
        };
        connectButton = new JButton("Connect");
        connectButton.addActionListener(buttonListener);
        connectButton.setEnabled(true);
        tempPanel.add(connectButton);
        setupPanel.add(tempPanel);
        setupConnection.setContentPane(setupPanel);
        setupConnection.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupConnection.setTitle("Setup Connection");
        setupConnection.setSize(500, 500);
        setupConnection.setLocationRelativeTo(null);
        setupConnection.pack();
        setupConnection.setVisible(true);
    }

    /**
     * A helper method that sets up the panel that will ask for the username
     * from the user.
     *
     * @param tempPanel a JPanel that will be the layout for getting username
     *                  from the user.
     *
     * @return JPanel will be used to build nickname layout
     */
    private JPanel nickLayout(JPanel tempPanel) {
        // nick name layout
        tempPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tempPanel.add(new JLabel("NickName"));
        nickName = new JTextField(10);
        nickName.setText(userName);
        nickName.setEnabled(true);
        nickName.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                nickName.selectAll();
                userName = nickName.getText();
            }
        });
        tempPanel.add(nickName);
        return tempPanel;
    }

    /**
     * A helper method that sets up the panel tha will ask for the username
     * from the user.
     *
     * @param tempPanel a JPanel that will be the layout for getting host
     *                  address from the user.
     *
     * @return JPanel that will be used to build hostname layout
     */
    private JPanel hostLayout(JPanel tempPanel) {
        // host name layout
        tempPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tempPanel.add(new JLabel("HostName"));
        hostName = new JTextField(10);
        hostName.setText(hostname);
        hostName.setEnabled(true);
        hostName.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                hostName.selectAll();
                hostname = hostName.getText();
            }
        });
        tempPanel.add(hostName);
        return tempPanel;
    }

    /**
     * A helper method that sets up the panel that will ask for the port
     * number from the user.
     *
     * @param tempPanel a JPanel that will be the layout for getting the port
     *                  number from the user.
     *
     * @return JPanel that will be used to build port layout
     */
    private JPanel portLayout(JPanel tempPanel) {
        // Port layout
        String tempString = "";
        tempPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tempPanel.add(new JLabel("Port#"));
        portNum = new JTextField(10);
        portNum.setText(tempString);
        portNum.setEnabled(true);
        portNum.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                portNum.selectAll();
                port = Integer.parseInt(portNum.getText());
            }
        });
        tempPanel.add(portNum);
        return tempPanel;
    }

    /**
     * Method that sets up the chat box that will take user messages and send
     * them out.
     *
     * @param clientw a ClientWrap that wraps the NetworkInterface up, this is
     *                so it can be declared as "final" and used in inside 
     *                classes.
     */
    public void guiChat(ClientWrap clientw) {
        final MultiuserChatClient newClient = clientw.client;
        chatFrame = new JFrame();
        chatPanel = new JPanel(new GridLayout(3, 1));
        JPanel tempPanel = null; // used to setup rows
        tempPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        conversation.setLineWrap(true);
        conversation.setEditable(false);
        final JScrollPane chatTextPane = new JScrollPane(conversation,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.addMessageListener(this);
        userInput = new JTextField();
        userInput.setEnabled(true);
        userInput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = "";
                boolean disconnect = false;
                msg = userInput.getText();
                    if (!msg.equals("")) {
                        userInput.selectAll();
                        newClient.send(msg);
                        if (msg.equals("/quit")) {
                            disconnect = true;
                            chatFrame.dispose();
                            setupConnection.dispose();
                            System.exit(0);
                        }
                    }
            }
        });
        chatPanel.add(userInput, BorderLayout.SOUTH);
        chatPanel.add(chatTextPane, BorderLayout.CENTER);
        chatPanel.setPreferredSize(new Dimension(500, 500));
        chatFrame = new JFrame("MultiUserChatClient");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setContentPane(chatPanel);
        chatFrame.setLocationRelativeTo(null);
        chatFrame.pack();
        chatFrame.setVisible(true);
    }

    static class ClientWrap {

        /* A field to store the MultiuserClient that will be wrapped */
        public MultiuserChatClient client;

        /**
         * Constructor for the class it initializes the fields.
         * 
         * @param client a MultiuserChatClient that will be wrapped
         */
        public ClientWrap(MultiuserChatClient client) {
            this.client = client;
        }

        /**
         * A getter method.
         *
         * @return MultiuserChatClient
         */
        public MultiuserChatClient getClient() {
            return client;
        }
    }

    /**
     * Method closes a messagelistener that corresponds the the specified
     * source.
     *
     * @param source a MessageSource that specifies which MessageListener will
     *               be removed.
     */
    public void sourceClosed(MessageSource source) {
        source.removeMessageListener(this);
    }

    /**
     * Method notifies that a message was recieved.
     *
     * @param message a String containing the message.
     * @param source  a MessageSource the message came from.
     */
    public void messageReceived(String message, MessageSource source) {
        notifyReceipt(message);
    }

    /**
     * The main method, it provides an entry point into the program. It does
     * not use any command line arguments
     */
    public static void main(String[] args) {
        GuiDriver test = new GuiDriver();
    }
}
