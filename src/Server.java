import java.net.*;
import java.text.ParseException;
import java.io.*;

/**
 * Class has not changed since coursework 2
 * <p>
 * The server hosts the game and allows the clients to interact with game. Eg move on the map. It creates an instance of
 * com.daniel.dod.GameLogic to do this.
 * <p>
 * This class is a heavily modified version of the Java KnockKnock
 * TUTORIAL.
 *
 * @Author Daniel Jenkyn
 */
public class Server {
    /**
     * Constructs a new instance of the Server and starts a client thread.
     * <p>
     * The server is constantly listening for new player to join.
     */
    public Server(GameLogic game, int portNumber) throws IOException {

        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new ClientThread(serverSocket.accept(), game);
            }
            serverSocket.close();
            System.out.println("Socket closed.");
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            listening = false;
            System.exit(-1);
        }
    }

    /**
     * When the server is run first time a new instance of gamelogic is created. The server can use a different map if
     * the user desires.
     */
    public static void main(String[] args) {
        try {
            GameLogic game = null;
            switch (args.length) {
                case 0:
                    // No Command line arguments - default map
                    System.out.println("Starting Game with Default Map");
                    game = new GameLogic("res/maps/defaultMap");
                    break;
                case 1:
                    System.out.println("Starting Game with Map " + args[0]);
                    game = new GameLogic(args[0]);
                    break;
            }
            //Port number for server
            new Server(game, 54879);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}