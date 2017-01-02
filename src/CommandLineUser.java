import java.util.ArrayList;
import java.util.List;
/**
 * An abstract class to handle the parsing and handling of textual commands,
 * e.g. MOVE and PICKUP.
 * 
 * This class could be extended to provide a server instance for every user who
 * connects over the network.
 * 
 */
public abstract class CommandLineUser implements PlayerListener, Runnable {
	// The game which the command line user will operate on.
	// This is private to enforce the use of "processCommand".
	private final GameLogic game;

	// The player must be added onnto the map. Initially it is not.
	private boolean playerAdded = false;

	//The ID of the player on the map.
	int playerID = -1;

	// In order to ensure the specification is met, we need to ensure that
	// a response is sent after a command, before anything else is sent.
	// Therefore, we use "waitingForResponse" as a flag for this, and
	// store any other messages, e.g. shouts from players, in a List.
	// Due to network latency, this could still happen at the client side. 
	private boolean waitingForResponse = false;
	private final List<String> messageBuffer;

	CommandLineUser(GameLogic game) {
		this.game = game;
		this.messageBuffer = new ArrayList<String>();
	}

	/**
	 * Allows the class to be "run" by a thread, in the matter specified by the
	 * sub-class.
	 * 
	 * Perhaps this could be used to service a new client on the network?
	 */
	@Override
	public abstract void run();

	/**
	 * Sends a message to the player from the game.
	 * 
	 * @ param message The message to be sent
	 */
	@Override
	public void sendMessage(String message) {
		outputMessage("MESSAGE " + message, false);
	}

	@Override
	public void sendChange() {
		outputMessage("CHANGE", false);
	}

	/**
	 * Method is used to call change when player first enters game.
	 */
	protected void manualChange(){
		this.game.clientChange();
	}

	/**
	 * Informs the user of the beginning of a player's turn
	 */
	@Override
	public void startTurn() {
		outputMessage("STARTTURN", false);
	}

	/**
	 * Informs the user of the end of a player's turn
	 */
	@Override
	public void endTurn() {
		outputMessage("ENDTURN", false);
	}

	/**
	 * Informs the user that the player has won
	 */
	@Override
	public void win() {
		outputMessage("WIN", false);
	}

	/**
	 * Informs the user that the player's hit points have changed
	 */
	@Override
	public void hpChange(int value) {
		outputMessage("HITMOD " + value, false);
	}

	@Override
	public void apRemaining(int value) {
		outputMessage("AP " + value, false);
	}
	/**
	 * Informs the user that the player's gold count has changed
	 */
	@Override
	public void treasureChange(int value) {
		outputMessage("TREASUREMOD " + value, false);
	}

	/**
	 * Processes a text command from the user.
	 * 
	 * @param commandString
	 *            the string containing the command and any argument
	 */
	protected final void processCommand(String commandString) {
		if(commandString.length() == 0) {
		
			return;
		}

		// Process the command string e.g. MOVE N
		final String commandStringSplit[] = commandString.split(" ", 2);
		final String command = commandStringSplit[0];
		final String arg = ((commandStringSplit.length == 2)
				? commandStringSplit[1]
						: null);

		try {
			processCommandAndArgument(command, arg);
		} catch (final CommandException e) {
			outputMessage("FAIL " + e.getMessage(), true);
		}
	}

	/**
	 * Adds the player to the game. This could not be done in the constructor
	 * because the sub-class must be properly constructed first in some cases,
	 * e.g. on a network.
	 * 
	 */
	protected void addPlayer() {
		if (this.playerAdded) {
			throw new RuntimeException("Player already added");
		}
		this.playerAdded = true;
		// The first message must be GOLD
		outputMessage("GOLD " + this.game.getGoal(), true);

		// Ensures that the instance will listen to the player in the
		// game for messages from the game
		this.playerID = this.game.addPlayer(this);

		//doOutputMessage("HELLO player");
	}

	protected void removePlayer() {

		if (!this.playerAdded) {
			throw new RuntimeException("Player not added");
		}
		this.playerAdded = false;

		this.game.removePlayer(this.playerID);
	}

	/**
	 * Inherited by the base class to handle outputting textual messages in the
	 * correct manner, e.g. printing to the screen or read by the bot.
	 * 
	 * Perhaps this could be used to send text over a network?
	 * 
	 * @param message Message to output or act upon
	 */
	protected abstract void doOutputMessage(String message);

	/**
	 * Processes the command and an optional argument
	 * 
	 * @param command
	 *            the text command
	 * @param arg
	 *            the text argument (null if no argument)
	 * @throws CommandException
	 */
	private void processCommandAndArgument(String command, String arg)
			throws CommandException {
		if (!this.playerAdded){
			throw new RuntimeException("Player not added");
		}

		if (command.equals("HELLO")){
			if (arg == null) {
				throw new CommandException("HELLO needs an argument");
			}

			this.waitingForResponse = true;
			this.game.clientHello(arg, this.playerID);
			outputMessage("HELLO " + arg, true);

		} else if (command.equals("LOOK")) {
			if (arg != null) {
				throw new CommandException("LOOK does not take an argument");
			}
			this.waitingForResponse = true;
			outputMessage("LOOKREPLY" + System.getProperty("line.separator")
					+ this.game.clientLook(this.playerID), true);

		} else if (command.equals("PICKUP")) {
			if (arg != null) {
				throw new CommandException("PICKUP does not take an argument");
			}

			this.waitingForResponse = true;
			this.game.clientPickup(this.playerID);

			outputSuccess();

		}else if (command.equals("MOVE")) {
			// We need to know which direction to move in.
			if (arg == null) {
				throw new CommandException("MOVE needs a direction");
			}

			this.waitingForResponse = true;
			this.game.clientMove(getDirection(arg), this.playerID);

			outputSuccess();

		} else if (command.equals("ATTACK")) {
			// We need to know which direction to move in.
			if (arg == null) {
				throw new CommandException("ATTACK needs a direction");
			}

			this.waitingForResponse = true;
			this.game.clientAttack(getDirection(arg), this.playerID);

			outputSuccess();

		} else if (command.equals("ENDTURN")) {
			this.game.clientEndTurn(this.playerID);

		} else if (command.equals("QUIT")) {
			this.game.removePlayer(this.playerID);

		} else if (command.equals("HELP")) {
			this.game.clientHelp(this.playerID);

		} else if (command.equals("SHOUT")) {
			// Ensure they have given us something to shout.
			if (arg == null) {
				throw new CommandException("need something to shout");
			}

			htmlCommands(arg);

		} else if (command.equals("CHANGE")) {
			// Ensure they have given us something to shout.
			if (arg != null) {
				throw new CommandException("Change does not need an argument");
			}
			this.game.clientChange();

		} else if (command.equals("SETPLAYERPOS")) {
			if (arg == null) {
				throw new CommandException("need a position");
			}

			// Obtain two co-ordinates
			final String coordinates[] = arg.split(" ");
			
			if (coordinates.length != 2) {
				System.out.println(coordinates[0] + coordinates.length);
				throw new CommandException("need two co-ordinates");
			}

			try {
				final int col = Integer.parseInt(coordinates[0]);
				final int row = Integer.parseInt(coordinates[1]);

				this.game.setPlayerPosition(col, row, this.playerID);
				outputSuccess();
			} catch (final NumberFormatException e) {
				throw new CommandException("co-ordinates must be integers");
			}

		} else {
			// If it is none of the above then it must be a bad command.
			throw new CommandException("CMD user Invalid command");
		}
	}

	/**
	 * Obtains a compass direction from a string. Used to ensure the correct
	 * exception type is thrown, and for consistancy between MOVE and ATTACK.
	 * 
	 * @param string
	 *            the direction string
	 * 
	 * @return the compass direction
	 * @throws CommandException
	 */
	private CompassDirection getDirection(String string)
			throws CommandException {
		try {
			return CompassDirection.fromString(string);
		} catch (final IllegalArgumentException e) {
			throw new CommandException("Invalid direction");
		}
	}

	/**
	 * Sends a success message in the event that a command has succeeded
	 */
	private void outputSuccess() {
		outputMessage("SUCCESS", true);
	}

	/**
	 * Outputs a message to the player, using the abstract "doOutputMessage"
	 * method, which allows the sub-class to handle it in different ways, e.g.
	 * simply printing it, or perhaps sending it over the network?
	 * 
	 * Since the specification says that the response to a command must be
	 * returned before anything else, this message will be saved and output to
	 * the player after the response.
	 * 
	 * @param message
	 *            the message to send to the player.
	 * @param isResponse
	 *            whether or not the message is a response to a command, e.g.
	 *            "SUCCESS" or "FAIL".
	 */
	private final void outputMessage(String message, boolean isResponse) {
		// If the user is waiting for a response, buffer the message
		if (this.waitingForResponse) {
			if (isResponse) {
				// Output the response
				doOutputMessage(message);

				// We can now send everything from the buffer and clear it
				for (final String line : this.messageBuffer) {
					doOutputMessage(line);
				}

				this.messageBuffer.clear();

				// We are no longer waiting for a response
				this.waitingForResponse = false;
			} else {
				// Add it to the buffer to be sent when the response has been
				// sent
				this.messageBuffer.add(message);
			}
		} else {
			// The user is not waiting for a response. Send it immediately.
			doOutputMessage(message);
		}
	}

	public void htmlCommands(String arg){
		String playername = this.game.getPlayerName(this.playerID);

		if (arg.startsWith("/bld")) {
			String[] inputArray = arg.split("/bld", 2);
			this.game.clientShout(playername + ": " + "<b>" + inputArray[1] + "</b>");
		} else if (arg.startsWith("/itl")) {
			String[] inputArray = arg.split("/itl", 2);
			this.game.clientShout(playername + ": " + "<i>" + inputArray[1] + "</i>");
		} else if (arg.startsWith("/g")) {
			String[] inputArray = arg.split("/g", 2);
			this.game.clientShout(playername + ": " + "<font color = green>" + inputArray[1] + "</font>");
		} else if (arg.startsWith("/r")) {
			String[] inputArray = arg.split("/r", 2);
			this.game.clientShout(playername + ": " + "<font color = red>" + inputArray[1] + "</font>");
		} else if (arg.startsWith("/o")) {
			String[] inputArray = arg.split("/o", 2);
			this.game.clientShout(playername + ": " + "<font color = orange>" + inputArray[1] + "</font>");
		} else if (arg.startsWith("/b")) {
			String[] inputArray = arg.split("/b", 2);
			this.game.clientShout(playername + ": " + "<font color = blue>" + inputArray[1] + "</font>");
		} else {
			this.game.clientShout(playername + ": " + arg );
		}
	}
}
