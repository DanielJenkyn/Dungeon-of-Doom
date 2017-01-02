import javax.swing.*;
import java.awt.event.*;

/**
 * The main purpose for this class is to listen for any form of input whether it be from button, keyboard or mouse.
 * It then acts upon these inputs by calling the appropriate method.
 *
 * This class belongs to DungeonOfDoom.
 *
 * @Author Daniel Jenkyn
 */
public class Controller implements ActionListener, KeyListener, MouseListener {

    private Client cl;
    private JTextField chatMsg;
    private JPanel masterPanel;

    /**
     * Constructor, unused.
     */
    public Controller() {
    }

    /**
     * Gets the client when we need it from the GUI class.
     *
     * @param cl instance of Client passed through from GUI.
     */
    public void setClient(Client cl){
        this.cl = cl;
    }

    /**
     * Gets the chatMsg when we need it from the GUI class.
     *
     * @param chatMsg chatMsg from GUI is passed through.
     */
    public void setChatBox(JTextField chatMsg){
        this.chatMsg = chatMsg;
    }

    /**
     * Gets the masterPanel when we need it from the GUI class.
     *
     * @param masterPanel masterPanel from GUI is passed through.
     */
    public void setMasterPanel(JPanel masterPanel){
        this.masterPanel = masterPanel;
    }

    /**
     * The buttons in GUI pass through the String action. That action can then be related to a client command.
     *
     * @param e ActionEvent from GUI
     */
    public void actionPerformed(ActionEvent e) {
        //Split at space into two args
        String[] inputArray = e.getActionCommand().split(" ", 2);

        if(inputArray[0].equals("hello")){
            cl.callHello(inputArray[1]);
        }else if(inputArray[0].equals("move")){
            cl.callMove(inputArray[1].substring(0, 1));
        }else if(inputArray[0].equals("pickup")){
            cl.callPickup();
        }else if (inputArray[0].equals("shout")){
            cl.callShout(chatMsg.getText());
            chatMsg.setText("");
        }else if(inputArray[0].equals("help")){
            cl.callHelp();
        }else if(inputArray[0].equals("endturn")){
            cl.callPlayerEndTurn();
        }

        //Press enter to send message across chat
        if(e.getSource() == chatMsg){
            cl.callShout(chatMsg.getText());
            chatMsg.setText("");
        }
    }
    /**
     * Unused KeyListener interface method
     */
    public void keyTyped(KeyEvent e) {

    }

    /**
     * KeyEvent can be related to commands in client
     *
     * @param e KeyEvent from GUI
     */
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            cl.callMove("w");
        }
        if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            cl.callMove("e");
        }
        if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            cl.callMove("n");
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            cl.callMove("s");
        }
        if(e.getKeyCode() == KeyEvent.VK_P) {
            cl.callPickup();
        }
        if(e.getKeyCode() == KeyEvent.VK_H) {
            cl.callHelp();
        }
        if(e.getKeyCode() == KeyEvent.VK_E) {
            cl.callPlayerEndTurn();
        }
    }

    /**
     * Unused KeyListener interface method
     */
    public void keyReleased(KeyEvent e) {
    }
    /**
     * Gives focus back to game, once used has finished with chat and click back on the game.
     * @param e
     */
    public void mouseClicked(MouseEvent e) {
        masterPanel.requestFocus();
    }
    /**
     * Unused MouseListener interface method
     */
    public void mousePressed(MouseEvent e) {

    }
    /**
     * Unused MouseListener interface method
     */
    public void mouseReleased(MouseEvent e) {

    }
    /**
     * Unused MouseListener interface method
     */
    public void mouseEntered(MouseEvent e) {

    }
    /**
     * Unused MouseListener interface method
     */
    public void mouseExited(MouseEvent e) {

    }
}
