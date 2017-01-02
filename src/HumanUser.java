import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is no used as part of coursework 3
 *
 * A class to allow a local human to play the game. A similar class could be
 * created for a network client, by obtaining commands from a network Socket and
 * relaying the messages back.
 */
public class HumanUser extends Client {

	public HumanUser(String host, int port) {
		super(host, port);

		System.out.println("Human" + hostname + ":" + portNumber);
		readFromKeyboard();
	}

	private void readFromKeyboard() {
		//READ COMMAND FROM KEYBOARD, IF ELSE WITH ALL THE COMMANDS

		// Process the command string e.g. MOVE N
		boolean keyboardEntry = true;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput = null;
		while(keyboardEntry){
			try {
				userInput = stdIn.readLine().toLowerCase();

				String[] inputArray = userInput.split(" " , 2);
				if(inputArray[0].equals("hello")){
					callHello(inputArray[1]);
				}else if(inputArray[0].equals("move")){
					callMove(inputArray[1].substring(0,1));
				}else if(inputArray[0].equals("look")){
					callLook();
				}else if(inputArray[0].equals("quit")){
					callQuit();
				}else if(inputArray[0].equals("help")){
					callHelp();
				}else if(inputArray[0].equals("pickup")){
					callPickup();
				}else if (inputArray[0].equals("setplayerpos")){
					callSetPlayerPos(inputArray[1]);
				} else if (inputArray[0].equals("shout")){
					callShout(inputArray[1]);
				}else if(inputArray[0].equals("endturn")){
					callPlayerEndTurn();
				}else {
					System.out.println("Invalid command, type help for a list of commands");
				}
			}catch (IOException e) {
				e.printStackTrace();
			}catch (StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException e) {
				System.err.println("Invalid command");
			}
		}
	}
}
