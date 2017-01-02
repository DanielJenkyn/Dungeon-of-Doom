import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The main purpose for this class is as a launcher for the main game, note this is launcher is only used human users and not
 * by the bot. The launcher allows the user to connect to any port number within range, connect to any server name and
 * choose any username they wish.
 *
 * This class belongs to DungeonOfDoom.
 *
 * @Author Daniel Jenkyn
 */
public class StartGame {
    private GUI gui;
    private ImageIcon frameIcon;

    //Constants
    private static String title = "Dungeon of Doom";
    private static int width = 650;
    private static int height = 100;
    //Highest port number = unsigned 16-bit integer
    private static int largestPortNum = 65535;

    private JFrame launcherFrame;
    private JPanel launcherPanel;
    private GridBagConstraints gbcLauncher;
    private JTextField hostInput, portInput, usernameInput;
    private JButton btnConnect;
    /**
     * Constructor
     *
     * Call createLauncherGUI method when new instance of StartGame is created.
     *
     */
    public StartGame() {
        createLauncherGUI();
    }
    /**
     * Creates and setups the GUI for the start game launcher.
     * Adds all GUI components to mainFrame (Program Window).
     *
     */
    public void createLauncherGUI() {
        launcherFrame = new JFrame(title);
        launcherFrame.setSize(width, height);
        frameIcon = new ImageIcon("res/images/frameIcon.png");
        launcherFrame.setIconImage(frameIcon.getImage());
        launcherFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        launcherPanel = new JPanel();
        launcherPanel.setLayout(new GridBagLayout());
        gbcLauncher = new GridBagConstraints();

        JLabel hostname = new JLabel("Enter Host Name:");
        gbcLauncher.gridx = 0;
        gbcLauncher.gridy = 0;
        gbcLauncher.gridwidth = 1;
        gbcLauncher.gridheight = 1;
        launcherPanel.add(hostname, gbcLauncher);

        hostInput = new JTextField(10);
        hostInput.setToolTipText("Server name");
        gbcLauncher.gridx = 1;
        gbcLauncher.gridy = 0;
        gbcLauncher.gridwidth = 1;
        gbcLauncher.gridheight = 1;
        launcherPanel.add(hostInput, gbcLauncher);

        JLabel portNumber = new JLabel("Enter Port Number:");
        gbcLauncher.gridx = 2;
        gbcLauncher.gridy = 0;
        gbcLauncher.gridwidth = 1;
        gbcLauncher.gridheight = 1;
        launcherPanel.add(portNumber, gbcLauncher);

        portInput = new JTextField(10);
        portInput.setToolTipText("Port from 0 - " + largestPortNum);
        gbcLauncher.gridx = 3;
        gbcLauncher.gridy = 0;
        gbcLauncher.gridwidth = 1;
        gbcLauncher.gridheight = 1;
        launcherPanel.add(portInput, gbcLauncher);

        JLabel name = new JLabel("Enter name:");
        gbcLauncher.gridx = 4;
        gbcLauncher.gridy = 0;
        gbcLauncher.gridwidth = 1;
        gbcLauncher.gridheight = 1;
        launcherPanel.add(name, gbcLauncher);


        usernameInput = new JTextField(10);
        gbcLauncher.gridx = 5;
        gbcLauncher.gridy = 0;
        gbcLauncher.gridwidth = 1;
        gbcLauncher.gridheight = 1;
        launcherPanel.add(usernameInput, gbcLauncher);

        btnConnect = new JButton("Connect");
        btnConnect.setToolTipText("Connect to server");
        addLauncherListener();
        gbcLauncher.gridx = 2;
        gbcLauncher.gridy = 1;
        gbcLauncher.gridwidth = 1;
        gbcLauncher.gridheight = 1;
        launcherPanel.add(btnConnect, gbcLauncher);

        launcherFrame.add(launcherPanel);
        showLauncherGUI();
    }
    /**
     * Displays the launcher with appropriate frame options.
     */
    public void showLauncherGUI() {
        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setResizable(false);
        launcherFrame.setVisible(true);
        launcherFrame.requestFocus();
    }
    /**
     * Adds action listener for connect button.
     * Handles user input errors and notifies user via a Pop up window
     * Connection error are also thrown up to this class and displayed a Pop up window
     */
    public void addLauncherListener() {
        /* Lambda expression could be used as we are implementing an interface
        and we can stop importing java.awt.event. However LCPU does not support this*/
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent a) {
                try {
                    if (hostInput.getText().trim().length() < 1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter a Host Name", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    } else if (portInput.getText().trim().length() < 1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter a Port Number", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    } else if (Integer.parseInt(portInput.getText().trim()) > largestPortNum) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter a lower than " + largestPortNum, "Connection Error", JOptionPane.ERROR_MESSAGE);
                    } else if (usernameInput.getText().trim().length() < 1) {
                        JOptionPane.showMessageDialog(new JFrame(), "Please enter a username", "Connection Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        gui = new GUI(hostInput.getText().trim(), Integer.parseInt(portInput.getText().trim()), usernameInput.getText().trim());
                        launcherFrame.setVisible(false);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Could not establish a connection", "Connection Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "This is not a valid port number", "Connection Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    /**
     * Main entrance point for any human user.
     */
    public static void main(String[] args) {
        StartGame game = new StartGame();
    }
}