import java.net.*;
import java.io.*;
/**
 *
 * Updated class so when player enters/exits game a look reply is sent.
 *
 * ClientThread spawns a client class for each player that joins.
 * 
 * Extends command line user so processCommand can pass through
 * commands to be carry out server side.
 *
 *
 * @Author Daniel Jenkyn
 */
public class ClientThread extends CommandLineUser implements Runnable{
    private Socket socket = null;
    private PrintWriter out;
    private BufferedReader in;
    /**
    * Constructs a new instance of the ClientThread.
	*
	* The constructor tries to establish a connection 
	* with the server. It also starts a thread.
	*/
	public ClientThread(Socket socket, GameLogic game) {
        super(game);
        try {
			this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true); //Output to the clients input stream.
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Output message is the in.
            addPlayer();
            Thread thread = new Thread(this);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	/**
	* Thread loop
	*
	* Constantly running to add createGameGUI new client for
	* new players that join the server. Sends a lookreply as soon as
     * player joins the game and when they leave.
	*/
    public void run() {
        manualChange();
        try {
			String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //Processes command in commandLine user
				processCommand(inputLine);
            }            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            removePlayer();
            manualChange();
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	/**
	* Sends message over to client side.
	*/
    protected void doOutputMessage(String message) {
        out.println(message);
    }
}