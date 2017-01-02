import java.io.*;
import java.net.*;
/**
 * Class has been updated to work with GUI.
 *
 * This client interacts with the server class. Both bot and
 * humanUser inherit client.
 *
 * This class is a heavily modified version of the Java KnockKnock
 * tutorial. 
 *
 * @Author Daniel Jenkyn
 */
public class Client implements Runnable {

	private GUI gui;
	private BufferedReader inputStream;
	private PrintWriter outputStream;

	private Socket ddSocket;
	
	protected static String hostname;
	protected static int portNumber;
	
	protected char[][] currentLookReply;
	protected int playerGold = 1;
	protected int goldRequired;
	protected boolean isGameRunning = true;

	//Html formatting
	private static String htmlBreak = "<br>";
	private static String htmlEndturn = "<font color = red>Endturn</font>";
	private static String htmlGetHello = "<font color = blue> has joined the game</font>";
	private static String htmlGetGold = "<font color = rgb(218,165,32)>Gold required to win: </font>";
	private static String htmlGetWin = "<font color=#FF0000>I</font><font color=#FFff00> </font><font " +
			"color=#00ff00>w</font><font color=#00ffff>i</font><font color=#0000ff>n</font>";
	private static String htmlGetStartTurn = "<font color = green>Your turn</font>";
	private static String htmlGetHitMod = "<font color =#FF69B4>Picked up health</font> ";
	private static String htmlGetTreasureMod = "<font color = rgb(218,165,32)>You have </font>";
	private static String htmlGetTreasureModEnd = "<font color = rgb(218,165,32)> gold</font>";
	private static String htmlGetAP = "<font color=#00d2ff>Remaing AP: </font>";
	private static String htmlGetFail = "<font color = red>Fail </font>";

	/**
    * Constructs a new instance of the Client. Using the GUI we give it.
	*
	* @param host hostname from GUI
	 * @param port portnumber form GUI
	 * @param gui GUI is passed through so we can update the map.
	*/
	public Client(String host, int port, GUI gui) {
		this.gui = gui;

		hostname = host;
		portNumber = port;
	}

	/**
	 * Overloaded Constructor for for the bot
	 *
	 * @param host hostname from GUI
	 * @param port portnumber form GUI
	 */
	public Client(String host, int port){
		hostname = host;
		portNumber = port;

		Socket ddSocket = null;

		try {
			ddSocket = new Socket(hostname, portNumber);
			outputStream = new PrintWriter(ddSocket.getOutputStream(), true);
			inputStream = new BufferedReader(new InputStreamReader(ddSocket.getInputStream()));
		}catch(UnknownHostException e) {
			System.err.println(hostname + " Doesn't appear to exist. Try checking the hostname");
			System.exit(1);
		}catch(IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + hostname);
			System.exit(1);
		}
		Thread myThread = new Thread(this);
		myThread.start();
	}
	/**
	 * This method is used so we can connect to the server only after the GUI has been made, rather than
	 * straight away in the constructor.
	 *
	 * @throws IOException
	 */
	public void connectToServer() throws IOException {
		ddSocket = new Socket(hostname, portNumber);
		outputStream = new PrintWriter(ddSocket.getOutputStream(), true);
		inputStream = new BufferedReader(new InputStreamReader(ddSocket.getInputStream()));

		Thread myThread = new Thread(this);
		myThread.start();
	}
	/**
	* Thread loop
	*
	* Thread allows concurrency between the client and the server,
	* and its reading from the server.
	*/
	public void run() {
		String input;
		boolean running = true;
		while(running) {
			try {
				input = inputStream.readLine();
				String[] args = input.split(" ", 2);
				switch (args[0]) {
					case "MESSAGE" : getMessage(args[1]);
						break;
					case "ENDTURN" : getEndTurn();
						break;
					case "HELLO" : getHello(args[1]);
						break;
					case "GOLD" : getGold(args[1]);
						break;
					case "WIN" : getWin();
						isGameRunning = false;
						break;
					case "LOSE" : getLose();
						break;
					case "STARTTURN" : getStartTurn();
						break;
					case "HITMOD" : getHitMod(args[1]);
						break;
					case "TREASUREMOD" : getTreasureMod();
						playerGold++;
						break;
					case "AP" : getAP(Integer.parseInt(args[1]));
						break;
					case "SUCCESS" : getSucceed();
						break;
					case "FAIL" : getFail(args[1]);
						break;
					case "LOOKREPLY" :
						getLookReply();
						break;
					case "CHANGE" :
						getChange();
						break;
					default:
						break;
					// for each possible input, run its method on args[1]
					// The output from the server to the user,
				}
			} catch (IOException e) {
				System.err.println("Server was closed or connection interrupted");
				e.printStackTrace();
				running = false;
				System.exit(-1);
			}
		}
	}	
	/**
	* Accessor methods
	* 
	* Retrieve from server to client
	*/
	private void getMessage(String message) {
		if(gui != null) {
			gui.setChatRoomText(message + htmlBreak);
		}
	}

	private void getEndTurn(){
		if(gui != null) {
			gui.setChatRoomText(htmlEndturn + htmlBreak);
		}
	}

	private void getChange(){
		callLook();
	}

	private void getHello(String message) {
		callShout(htmlGetHello);
	}
	
	private void getGold(String numberOfGold) {
		if(gui != null) {
			gui.setChatRoomText(htmlGetGold + numberOfGold + htmlBreak);
			goldRequired = Integer.parseInt(numberOfGold);
		}
	}
	
	private void getWin() {
		callShout(htmlGetWin);
	}
	
	private void getLose() {
		if(gui != null) {
			gui.setChatRoomText("Lose");
		}
	}
	
	private void getStartTurn() {
		if(gui != null) {
			gui.setChatRoomText(htmlGetStartTurn + htmlBreak);
		}
	}
	
	private void getHitMod(String number) {
		if(gui != null) {
			gui.setChatRoomText(htmlGetHitMod + number + htmlBreak);
		}
	}

	private void getTreasureMod() {
		if(gui != null) {
			gui.setChatRoomText(htmlGetTreasureMod + playerGold + htmlGetTreasureModEnd + htmlBreak);
		}
	}

	private void getAP(int amount){
		if(gui != null) {
			gui.setChatRoomText(htmlGetAP + amount + htmlBreak);
		}
	}
	
	private void getSucceed() {
		callChange();
	}
	
	private void getFail(String comment) {
		if(gui != null) {
			gui.setChatRoomText(htmlGetFail + comment + htmlBreak);
		}
	}
	
	public char[][] getLookReply() throws IOException {
		System.out.println("LOOKREPLY" );

		String line = inputStream.readLine();
		int width = line.length();
		currentLookReply = new char[width][width];

		for(int i = 0; i < width; i++) {
			currentLookReply[0][i] = line.charAt(i);
		}

		for(int j = 1; j < width; j++) {
			line = inputStream.readLine();
			for(int i = 0; i < width; i++) {
				currentLookReply[j][i] = line.charAt(i);
			}
		}


		if(gui != null) {
			gui.updateMap();
		} else {
			for(int j = 0; j < width; j++) {
				for(int i = 0; i < width; i++) {
					System.out.print(currentLookReply[j][i]);
				}
				System.out.println();
			}
			System.out.println();
		}
		return currentLookReply;
	}
	/**
	* Methods that communication from client TO sever
	* 
	*  A protected call method for every client TO server commands
	*/
	protected void callMove(String direction) {
		char charDirection = direction.toUpperCase().charAt(0);
		doOutputMessage("MOVE " + charDirection);
	}
	
	protected void callLook() {
		doOutputMessage("LOOK");
	}

	protected void callQuit() {
		doOutputMessage("QUIT");
	}

	protected void callHelp() {
		doOutputMessage("HELP");
	}

	protected void callPickup() {
		doOutputMessage("PICKUP");
	}
	
	protected void callSetPlayerPos(String x){
		doOutputMessage("SETPLAYERPOS "+ x);
	}
	
	protected void callHello(String name){
		doOutputMessage("HELLO " + name);
	}
	
	protected void callShout(String message){
		doOutputMessage("SHOUT " + message);
	}
	
	protected void callPlayerEndTurn(){
		doOutputMessage("ENDTURN");
	}
	
	protected void callChange() {
		doOutputMessage("CHANGE");
	}
	/**
	* Final method that sends from client TO server
	*/
	private void doOutputMessage(String message) {
		outputStream.println(message);
	}
}
	