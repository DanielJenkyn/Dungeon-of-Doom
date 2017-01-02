import java.util.Random;
/**
 * Only changed this class slightly to work with coursework 3. Added a main method.
 *
 * This class plays the game using my my AI from coursework on, if it cannot
 * path find it will then default to using the provided random bot move.
 *
 * Note that there is a (very slim) chance that it will go on forever because
 * of the random move method.
 *
 * The bot extends client just as the humanUser does, and interacts in the same way
 * a human user would.
 *
 * @Author Daniel Jenkyn
 */
public class Bot extends Client {
	private int[][] botPathMap;
	private boolean hasLantern = false;
	private boolean hasSword = false;
	private boolean hasArmour = false;

	//Pathfinding variables from my original AI
	private boolean prevMoveNorth;
	private boolean prevMoveSouth;
	private boolean prevMoveEast;
	private boolean prevMoveWest;

	private int pathFindNorth;
	private int pathFindEast;
	private int pathFindSouth;
	private int pathFindWest;
	/**
	 * Constructs a new instance of the Bot.
	 *
	 * @param game
	 *            The instance of the game, to run on. This is only needed for
	 *            the parent class, as the Bot sends text commands to it.
	 */
	public Bot(String host, int port) {
		super(host, port);

		this.prevMoveNorth = false;
		this.prevMoveSouth = false;
		this.prevMoveEast = false;
		this.prevMoveWest = false;

		this.pathFindNorth = pathFindNorth;
		this.pathFindEast = pathFindEast;
		this.pathFindSouth = pathFindSouth;
		this.pathFindWest = pathFindWest;

		//Bots name
		callHello("GLaDOS");
		while(isGameRunning) {
			lookAtMap();
			initializeBotPathMap();
			valueBotPathMap();
			pathFind();
		}
		callQuit();
	}
	/**
	 * Issues a LOOK to update what the bot can see. Returns when it is updated.
	 **/
	private void lookAtMap() {
		callLook();
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {

		}
		while (currentLookReply == null){
			try {
				Thread.sleep(200);
			} catch (final InterruptedException e) {

			}
		}
	}
	/**
	 * Assigns integer values to objects on the map so the bot
	 * can use them for path finding. Lower values are where the bot will seek
	 * to path find to. Objects of high value are avoided
	 */
	public void initializeBotPathMap() {

		int height = currentLookReply.length;
		int width = currentLookReply[0].length;
		int pheight = (currentLookReply.length-1)/2;
		int pwidth = (currentLookReply[0].length-1)/2;

		botPathMap = new int[height][width];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(currentLookReply[i][j] == 'E' && playerGold >= goldRequired) {
					botPathMap[i][j] = 0;
				}else if(currentLookReply[i][j] == 'G' ) {
					botPathMap[i][j] = 0;
				}else if(currentLookReply[i][j] == 'L' && this.hasLantern == false && playerGold >= goldRequired) {
					botPathMap[i][j] = 0;
				}else if (currentLookReply[i][j] == 'S' && this.hasSword == false && playerGold >= goldRequired) {
					botPathMap[i][j] = 0;
				}else if (currentLookReply[i][j] == 'A' && this.hasArmour == false && playerGold >= goldRequired) {
					botPathMap[i][j] = 0;
				}else if (currentLookReply[i][j] == 'H' && playerGold == goldRequired) {
					botPathMap[i][j] = 0;
				}else if(currentLookReply[i][j]  == '#' || currentLookReply[i][j] == 'X' || currentLookReply[i][j] == 'P') {
					botPathMap[i][j] = 88;
				}else {
					botPathMap[i][j] = 46;
				}
			}
		}
		//Uses centre point of look to pick up
		if(botPathMap[(botPathMap.length-1)/2][(botPathMap.length-1)/2] == 0) {
			pickupIfAvailable();
		}
	}
	/**
	 * Iterates a path out from the bot and creates a path of integers to follow.
	 * The Bounds checking is messy but I could not re-factor it in any way without
	 * the bot breaking, the only way I can think of that would require a fairly large redesign
	 * is by using recursion. I'd rather my code be readable and long rather than broken/unfinished.
	 */
	public void valueBotPathMap() {
		int destination = 0;
		int distance = 1;
		int pathCounter = 0;
		int maxCounterValue = 45;
		int path = 46;
		//Maximum number of iterations that can occur
		while(pathCounter < maxCounterValue) {
			for(int y = 0; y < botPathMap.length; y++) {
				for (int x = 0; x < botPathMap[0].length; x++) {
					if(botPathMap[y][x] == destination) {
						//Bounds checking on Bot look
						if(y > 0 && x < 4 && x > 0) {
							if(botPathMap[y-1][x] == path) {
								botPathMap[y-1][x] = distance;
							}
							if(botPathMap[y][x+1] == path) {
								botPathMap[y][x+1] = distance;
							}
							if(botPathMap[y][x-1] == path) {
								botPathMap[y][x-1] = distance;
							}
						}
						if(y < 4 && x < 4 && x > 0) {
							if(botPathMap[y+1][x] == path) {
								botPathMap[y+1][x] = distance;
							}
							if(botPathMap[y][x+1] == path) {
								botPathMap[y][x+1] = distance;
							}
							if(botPathMap[y][x-1] == path) {
								botPathMap[y][x-1] = distance;
							}
						}
						if(y < 4 && y > 0 && x > 0) {
							if(botPathMap[y+1][x] == path) {
								botPathMap[y+1][x] = distance;
							}
							if(botPathMap[y-1][x] == path) {
								botPathMap[y-1][x] = distance;
							}
							if(botPathMap[y][x-1] == path) {
								botPathMap[y][x-1] = distance;
							}
						}
						if(y < 4 && y > 0 && x < 4) {
							if(botPathMap[y+1][x] == path) {
								botPathMap[y+1][x] = distance;
							}
							if(botPathMap[y-1][x] == path) {
								botPathMap[y-1][x] = distance;
							}
							if(botPathMap[y][x+1] == path) {
								botPathMap[y][x+1] = distance;
							}
						}
					}
				}//end of inner for
			}//end of outer for
			//These increase within the while loop
			pathCounter++;
			destination++;
			distance++;
		}//End of while
	}
	/**
	 * Moves the bot in accordance to the lowest valued path, the lowest valued path
	 * is shortest path within look to the gold/exit.
	 */
	public void pathFind() {
		pathFindNorth = botPathMap[((botPathMap.length-1)/2)-1][(botPathMap.length-1)/2];
		pathFindEast = botPathMap[(botPathMap.length-1)/2][((botPathMap.length-1)/2)+1];
		pathFindSouth = botPathMap[((botPathMap.length-1)/2)+1][(botPathMap.length-1)/2];
		pathFindWest = botPathMap[(botPathMap.length-1)/2][((botPathMap.length-1)/2)-1];
		//Finds lowest value path
		int lowestValue = Math.min(Math.min(pathFindNorth,pathFindEast),Math.min(pathFindSouth,pathFindWest));

		//If there is a way to destination...
		if(lowestValue != 46) {
			if(pathFindNorth == lowestValue) {
				callMove("N");
			} else if(pathFindEast == lowestValue){
				callMove("E");
			} else if(pathFindSouth == lowestValue){
				callMove("S");
			} else if(pathFindWest == lowestValue){
				callMove("W");
			}
		} else {
			//Last resort of random move is called
			makeRandomMove();
		}
	}
	/**
	 * Makes a random move, not into a wall
	 */
	private void makeRandomMove() {
		try {
			final char dir = generateRandomMove();
			final String moveString = "MOVE " + dir;
			String direction = "" + dir;
			System.out.println(moveString);
			callMove(direction);

		}catch (final IllegalStateException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	/**
	 * Return a direction to move in. Note that we do checks to see what it in
	 * the square before sending the request to move to the game logic.
	 *
	 * @return direction in which to move
	 */
	private char generateRandomMove() {
		// First, ensure there is a move
		if (!isMovePossible()) {
			System.out.println("SHOUT I am stuck and so will terminate.");
			callShout("SHOUT I am stuck and so will terminate.");

			throw new IllegalStateException("The bot is stuck in a dead end and cannot move anymore!");
		}

		final Random random = new Random();
		while (true) {
			final int dir = (int) (random.nextFloat() * 4F);

			switch (dir) {
				case 0 : // N
					if (getSquareWithOffset(0, -1) != '#') {
						return 'N';
					}
					break;

				case 1 : // E
					if (getSquareWithOffset(1, 0) != '#') {
						return 'E';
					}
					break;

				case 2 : // S
					if (getSquareWithOffset(0, 1) != '#') {
						return 'S';
					}
					break;

				case 3 : // W
					if (getSquareWithOffset(-1, 0) != '#') {
						return 'W';
					}
					break;
			}
		}
	}
	/**
	 * Picks up anything the bot is standing on, if possible
	 */
	private void pickupIfAvailable() {
		switch (getCentralSquare()) {
			// We can't pick these up if we already have them, so don't even try
			case 'A' :
				if (!this.hasArmour) {
					callPickup();
					// We assume that this will be successful, but we could
					// check
					// the reply from the game.
					this.hasArmour = true;
				}
				break;
			case 'L' :
				if (!this.hasLantern) {
					callPickup();
					this.hasLantern = true;
				}
				break;
			case 'S' :
				if (!this.hasSword) {
					callPickup();
					this.hasSword = true;
					System.out.println("SHOUT I am a killer robot now");
				}
				break;
			// We'll always get some gold or health
			case 'G' :
				callPickup();
				System.out.println("SHOUT I got some gold");
				break;
			case 'H' :
				callPickup();
				break;
			default :
				break;
		}
	}
	/**
	 * Obtains the square in the centre of the LOOKREPLY, i.e. that over which
	 * the bot is standing
	 *
	 * @return the square under the bot
	 */
	private char getCentralSquare() {
		// Return the square with 0 offset
		return getSquareWithOffset(0, 0);
	}
	/**
	 * Obtains a square in of the LOOKREPLY with an offset to the bot
	 *
	 * @return the square corresponding to the bot and offset
	 */
	private char getSquareWithOffset(int xOffset, int yOffset) {
		final int lookReplySize = currentLookReply.length;
		final int lookReplyCentreIndex = lookReplySize / 2; // We rely on
		// truncation
		return currentLookReply[lookReplyCentreIndex + yOffset][lookReplyCentreIndex + xOffset];
	}
	/**
	 * Check if the there is a possible move from the centre of the vision field
	 * to another tile
	 *
	 * @return true if the bot is not encircled with walls, false otherwise
	 */
	private boolean isMovePossible() {
		if ((getSquareWithOffset(-1, 0) != '#')
				|| (getSquareWithOffset(0, 1) != '#')
				|| (getSquareWithOffset(1, 0) != '#')
				|| (getSquareWithOffset(0, -1) != '#')) {
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		Bot bot = new Bot(args[0], Integer.parseInt(args[1]));
	}
}
