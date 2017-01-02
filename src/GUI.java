import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * The main purpose for this class is to createGameGUI a GUI which acts as a "layer" over the client. This class contains no
 * actionListeners as it adheres by the MVC model.
 *
 * This class belongs to DungeonOfDoom.
 *
 * @Author Daniel Jenkyn
 */
public class GUI {

    private Client client;
    private Controller controller;

    private JFrame mainFrame;
    private JPanel masterPanel, mapPanel, chatPanel, buttonPanel;
    private JButton btnMoveNorth, btnMoveSouth, btnMoveEast, btnMoveWest, btnPickup, btnEndturn, btnHelp, btnSendMsg;
    private JTextField chatMsg;
    private JTextPane chatRoom;
    private String chatHistory = "Welcome to Dungeon of Doom chat!<br>";
    private ImageIcon frameIcon, playerTile, enemyPlayerTile, goldTile, swordTile, armourTile, healthTile, exitTile, lanternTile, wallTile, floorTile;
    private JLayeredPane playerPlane;
    private JLabel secondaryFloor, playerFloor;

    private GridBagConstraints gbcMaster, gbcChat;
    //Constants
    private static String title = "Dungeon of Doom";
    private static int frameWidth = 870;
    private static int frameHeight = 510;
    //Size of art 64 by 64 pixels
    private static int tileSize = 64;
    private static int mapGridWidth = 7;
    private static int mapGridHeight = mapGridWidth;

    /**
     * Constructor
     * <p>
     * The constructor creates a new instance of client and sends this over to be used by the Controller class. It also
     * only connects to the server once a GUI has been created so that this GUI can then load the map. The username is
     * then sent across the server.Only after these two stages can the GUI then be displayed to the user.
     *
     * @param hostname   the server name
     * @param portNumber the port number of the server
     * @param username   the users chosen name
     * @throws IOException
     */
    public GUI(String hostname, int portNumber, String username) throws IOException {
        client = new Client(hostname, portNumber, this);

        controller = new Controller();
        controller.setClient(client);

        createGameGUI();
        client.connectToServer();
        client.callHello(username);
        showGameGUI();
    }

    /**
     * This method is mainly for keeping the code looking clean. Calls all methods for constructing the overall GUI.
     */
    public void createGameGUI() {
        //Frame setup.
        createFrame();
        //masterPanel contains all other lesser panels
        createMasterPanel();
        //mapPanel contains all components regarding the map
        createMapPanel();
        //chatPanel contains all components regarding the chat
        createChatPanel();
        //buttonPanel contains all components regarding the user interaction
        createButtonPanel();
        //The master panel is then added to the frame.
        mainFrame.add(masterPanel);
    }

    /**
     * This method creates the frame, and sets it up.
     */
    public void createFrame() {
        mainFrame = new JFrame(title);
        mainFrame.setSize(frameWidth, frameHeight);
        frameIcon = new ImageIcon("res/images/frameIcon.png");
        mainFrame.setIconImage(frameIcon.getImage());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * This method creates the masterPanel, which uses GridBagLayout and contains all other panels
     */
    public void createMasterPanel() {
        masterPanel = new JPanel();
        masterPanel.addMouseListener(controller);
        masterPanel.addKeyListener(controller);
        controller.setMasterPanel(masterPanel);
        masterPanel.setLayout(new GridBagLayout());
        masterPanel.setBackground(Color.DARK_GRAY);
        gbcMaster = new GridBagConstraints();
        gbcMaster.anchor = GridBagConstraints.NORTHWEST;
    }

    /**
     * This method creates the mapPanel, which uses a 7*7 GridLayout and initialises all images for the map.
     */
    public void createMapPanel() {
        mapPanel = new JPanel();
        mapPanel.setLayout(new GridLayout(mapGridWidth, mapGridHeight));
        mapPanel.setPreferredSize(new Dimension((tileSize * mapGridWidth), (tileSize * mapGridHeight)));
        mapPanel.setBackground(Color.DARK_GRAY);
        //All images are contained in the res folder, they are initialised here but loaded in the upadteMap method.
        playerTile = new ImageIcon("res/images/playerTile.png");
        enemyPlayerTile = new ImageIcon("res/images/enemyPlayerTile.png");
        goldTile = new ImageIcon("res/images/goldTile.gif");
        swordTile = new ImageIcon("res/images/swordTile.png");
        armourTile = new ImageIcon("res/images/armourTile.png");
        healthTile = new ImageIcon("res/images/healthTile.png");
        exitTile = new ImageIcon("res/images/exitTile.png");
        lanternTile = new ImageIcon("res/images/lanternTile.gif");
        wallTile = new ImageIcon("res/images/wallTile.png");
        floorTile = new ImageIcon("res/images/floorTile.png");

        gbcMaster.gridx = 0;
        gbcMaster.gridy = 0;
        gbcMaster.gridwidth = 1;
        gbcMaster.gridheight = 2;
        gbcMaster.weightx = 0.10;
        gbcMaster.weighty = 0.80;
        masterPanel.add(mapPanel, gbcMaster);
    }

    /**
     * This method creates the chatPanel, which uses GridBagLayout and contains the chatRoom, chatMsg and btnSendMsg.
     */
    public void createChatPanel() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        gbcChat = new GridBagConstraints();
        chatPanel.setBackground(Color.DARK_GRAY);

        chatRoom = new JTextPane();
        chatRoom.setToolTipText("Messages appear here");
        //Setup using html so rich text can be used within chat
        chatRoom.setContentType("text/html");
        chatRoom.setEditable(false);
        gbcChat.gridx = 1;
        gbcChat.gridy = 0;
        gbcChat.gridwidth = 3;
        gbcChat.gridheight = 1;
        gbcChat.fill = GridBagConstraints.BOTH;
        gbcChat.weightx = 0.30;
        gbcChat.weighty = 0.98;
        chatPanel.add(new JScrollPane(chatRoom), gbcChat);

        chatMsg = new JTextField();
        chatMsg.setToolTipText("Type here to chat");
        chatMsg.addActionListener(controller);
        controller.setChatBox(chatMsg);
        chatMsg.setEditable(true);
        gbcChat.gridx = 0;
        gbcChat.gridy = 1;
        gbcChat.gridwidth = 2;
        gbcChat.gridheight = 1;
        gbcChat.fill = GridBagConstraints.BOTH;
        gbcChat.weightx = 0.815;
        gbcChat.weighty = 0.01;
        chatPanel.add(chatMsg, gbcChat);

        btnSendMsg = new JButton("Send");
        btnSendMsg.setToolTipText("Press button to send message");
        btnSendMsg.addActionListener(controller);
        btnSendMsg.setActionCommand("shout " + chatMsg.getText());
        gbcChat.gridx = 2;
        gbcChat.gridy = 1;
        gbcChat.gridwidth = 1;
        gbcChat.gridheight = 1;
        gbcChat.weightx = 0.185;
        gbcChat.weighty = 0.01;
        chatPanel.add(btnSendMsg, gbcChat);

        gbcMaster.gridx = 1;
        gbcMaster.gridy = 0;
        gbcMaster.gridwidth = 1;
        gbcMaster.gridheight = 1;
        gbcMaster.fill = GridBagConstraints.BOTH;
        gbcMaster.weightx = 0.90;
        gbcMaster.weighty = 0.100;
        masterPanel.add(chatPanel, gbcMaster);
    }

    /**
     * This method creates the buttons that displayed along the bottom of the GUI.
     */
    private void createButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 7));
        buttonPanel.setBackground(Color.DARK_GRAY);

        btnMoveNorth = new JButton("Move North");
        btnMoveNorth.setToolTipText("Movement");
        btnMoveSouth = new JButton("Move South");
        btnMoveSouth.setToolTipText("Movement");
        btnMoveEast = new JButton("Move East");
        btnMoveEast.setToolTipText("Movement");
        btnMoveWest = new JButton("Move West");
        btnMoveWest.setToolTipText("Movement");
        btnPickup = new JButton(" Pickup ");
        btnPickup.setToolTipText("Pickup item on the ground");
        btnHelp = new JButton("Help");
        btnEndturn = new JButton("Endturn");
        btnEndturn.setToolTipText("Endturn prematurely");

        //Buttons pass commands through to the actionListener
        btnMoveNorth.addActionListener(controller);
        btnMoveNorth.setActionCommand("move n");
        btnMoveSouth.addActionListener(controller);
        btnMoveSouth.setActionCommand("move s");
        btnMoveEast.addActionListener(controller);
        btnMoveEast.setActionCommand("move e");
        btnMoveWest.addActionListener(controller);
        btnMoveWest.setActionCommand("move w");
        btnPickup.addActionListener(controller);
        btnPickup.setActionCommand("pickup");
        btnEndturn.addActionListener(controller);
        btnEndturn.setActionCommand("endturn");
        btnHelp.addActionListener(controller);
        btnHelp.setActionCommand("help");

        buttonPanel.add(btnMoveNorth);
        buttonPanel.add(btnMoveSouth);
        buttonPanel.add(btnMoveWest);
        buttonPanel.add(btnMoveEast);
        buttonPanel.add(btnPickup);
        buttonPanel.add(btnEndturn);
        buttonPanel.add(btnHelp);

        gbcMaster.gridx = 0;
        gbcMaster.gridy = 2;
        gbcMaster.gridwidth = 2;
        gbcMaster.gridheight = 1;
        gbcMaster.weightx = 0.85;
        gbcMaster.weighty = 0.2;
        masterPanel.add(buttonPanel, gbcMaster);
    }

    /**
     * This method shows the GUI once all the panels have been setup.
     */
    public void showGameGUI() {
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setVisible(true);
        mainFrame.requestFocus();
        //Focus on the mapPanel so that KeyListeners can be used to control the player
        masterPanel.requestFocus();
    }

    /**
     * This method setups up the chatRoom, passes through previous value and add on current message
     * as .append cannot be used with JTextPane component.
     *
     * @param userMessage message that the user wishes to send through chat
     */
    public void setChatRoomText(String userMessage) {
        chatHistory = chatHistory + userMessage;
        chatRoom.setText(chatHistory);
        chatRoom.setCaretPosition(chatRoom.getDocument().getLength());
    }

    /**
     * This method as well as updating the map adds a JLayered pane so we can display the player on a separate layer
     * to the map. After the JLayered pane has been created we can the update the map "underneath" the player.
     */
    public void updateMap() {
        mapPanel.removeAll();

        int offset = -1;
        if (client.currentLookReply.length == 7) {
            offset = 0;
        }

        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 7; x++) {
                try {
                    if (y == 3 && x == 3) {
                        playerPlane = new JLayeredPane();
                        secondaryFloor = addMapIcons(client.currentLookReply[3 + offset][3 + offset]);
                        secondaryFloor.setBounds(0, 0, tileSize, tileSize);
                        playerFloor = new JLabel(playerTile);
                        playerFloor.setBounds(0, 0, tileSize, tileSize);
                        playerPlane.add(secondaryFloor, JLayeredPane.DEFAULT_LAYER);
                        playerPlane.add(playerFloor, JLayeredPane.PALETTE_LAYER);
                        mapPanel.add(playerPlane);
                    } else {
                        mapPanel.add(addMapIcons(client.currentLookReply[y + offset][x + offset]));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    //When there is a 5*5 look, surround with blank labels.
                    mapPanel.add(new JLabel());
                }
            }
        }
        //Recalculates layout
        mapPanel.revalidate();
    }

    /**
     * This method is essentially a helper function for updateMap. updateMap passes through the chars that must
     * converted from chars to JLabels containing icons for the map.
     *
     * @param arrayChar char from intial lookReply that must be replaced with an icon.
     * @return JLabel
     */
    public JLabel addMapIcons(char arrayChar) {
        if (arrayChar == 'L') return new JLabel(lanternTile);
        else if (arrayChar == 'S') return new JLabel(swordTile);
        else if (arrayChar == 'H') return new JLabel(healthTile);
        else if (arrayChar == 'A') return new JLabel(armourTile);
        else if (arrayChar == 'G') return new JLabel(goldTile);
        else if (arrayChar == '#') return new JLabel(wallTile);
        else if (arrayChar == '.') return new JLabel(floorTile);
        else if (arrayChar == 'E') return new JLabel(exitTile);
        else if (arrayChar == 'P') return new JLabel(enemyPlayerTile);
        else return new JLabel();
    }
}
