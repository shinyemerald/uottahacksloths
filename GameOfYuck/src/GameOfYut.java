import java.util.ArrayList;
import java.util.Scanner;

/**
 * Console based implementation of the game of yut (some traditional Korean game, also found in MapleStory with twisted rules).
 * Missing some functionality such as returning opponent's pieces back to their hands when you land on their spots.
 * I'm too tired to include all the rules in this java doc so you can check them out on wikipedia.
 *
 */
public class GameOfYut {
	public static final int NO_LANE = 0;
	public static final int LANE_AB = 1;
	public static final int LANE_BC = 2;
	public static final int LANE_CD = 3;
	public static final int LANE_DA = 4;
	public static final int LANE_BD = 5;
	public static final int LANE_CA = 6;
	public static final int FINISHED = 7;
	
	private static Player[] players;
	private static Player currentPlayer;
	private static Scanner input;
	
	/**
	 * Initialize a game of yut. Automatically starts it, for now, since there is no GUI that is implemented.
	 */
	public GameOfYut() {
		// for user input from the console later
		input = new Scanner(System.in);
		
		// 2 players, for now
		players = new Player[2];
		players[0] = new Player(1);
		players[1] = new Player(2);
		playGame();
		input.close();
	}
	
	/**
	 * Cycle through the players and allow them to "roll" the sticks and execute their desired moves.
	 */
	private void playGame() {
		for (currentPlayer = players[0]; ; currentPlayer = (currentPlayer.equals(players[1])) ? players[0] : players[1]) {
			rollPhase();
			movePhase(currentPlayer.numRolls());
		}
	}
	
	/**
	 * The roll phase is when the player uses RNG to determine how many moves they're allowed.
	 * Rolling 4 of a kind will let the play be able to move a piece 5 spaces, for 4 downward facing sticks, or 4 spaces, for
	 * 4 upward facing sticks. Getting 4 of a kind also lets the player get more rolls for the turn. The player stops rolling when
	 * they roll less than 4 of a kind.
	 * 
	 * Prints out a string in the console to let the user know what rolls the player got.
	 */
	private void rollPhase() {
		boolean rollAgain = true;
		while (rollAgain) {
			int roll = currentPlayer.roll();
			
			// check if not 4 of a kind
			if (roll != 4 && roll != 5)
				rollAgain = false;
		}
		System.out.println("Player" + currentPlayer.getNumber() + " has rolled " + currentPlayer.getStringRolls());
	}
	
	/**
	 * After accumulating some roll(s), the player must move their pieces. A player is able to move any piece using any of the
	 * accumulated rolls that they got in the same turn.
	 * 
	 * @param numberOfRolls the number of accumulated rolls that the currentl player has acquired in the current turn
	 */
	private void movePhase(int numberOfRolls) {
		for (int i = 0; i < numberOfRolls; i++) { 
			// user input
			int move = input.nextInt();
			int index = -1;
			while (index == -1) {
				// want to make sure the pieces that the player wants to move haven't already cleared the board
				index = input.nextInt();
				if (currentPlayer.wonnered(index))
					index = -1;
			}
			
			// save the location of the piece we're going to move
			// you can move a stack of pieces if they're all starting from the same spot
			ArrayList<GamePiece> otherPieces = currentPlayer.getPieces();
			GamePiece lastPiece = otherPieces.remove(index);
			int lastIndex = lastPiece.getIndex();
			int lastLane = lastPiece.getLane();
			movePiece(move, index);
			currentPlayer.useRoll(move);
			
			// cycles through the other pieces held by the current player if they were on the spot that we just moved
			for (GamePiece piece : otherPieces) {
				if (lastIndex == piece.getIndex() && lastLane == piece.getLane() && lastLane != NO_LANE && piece.getLane() != FINISHED)
					movePiece(move, piece.getIndex());
			}
		}
	}
	
	/**
	 * Moves the nth piece held by the current player m spaces. This method contains a bunch of (questionable) logic that (possibly) ensures the correct path that pieces
	 * may go around the board.
	 * 
	 * @param m number of spaces that we want the piece to go
	 * @param n the index of the piece that we want to move
	 */
	private void movePiece(int m, int n) {
		int newIndex = 0;
		boolean newLane = true;
		int laneLength = (currentPlayer.getPiece(n).getLane() == LANE_BD || currentPlayer.getPiece(n).getLane() == LANE_CA) ? 6 : 5;
		if (currentPlayer.getPiece(n).getLane() == LANE_BD && m + currentPlayer.getPiece(n).getIndex() - 3 == 0)
			newIndex = 0;
		else if (m + currentPlayer.getPiece(n).getIndex() >= laneLength)
			newIndex =  m + currentPlayer.getPiece(n).getIndex() - laneLength;
		else {
			newIndex = m + currentPlayer.getPiece(n).getIndex();
			if (currentPlayer.getPiece(n).getLane() != NO_LANE)
				newLane = false;
		}
		
		if (newIndex == 0 && currentPlayer.getPiece(n).getLane() == LANE_BD) {
			currentPlayer.setLane(n, LANE_CA);
			currentPlayer.setIndex(n, 3);
		} else if (newIndex == 0 && currentPlayer.getPiece(n).getLane() == LANE_AB) {
			currentPlayer.setLane(n, LANE_BD);
			currentPlayer.setIndex(n, newIndex);
		} else if (newIndex == 0 && currentPlayer.getPiece(n).getLane() == LANE_BC) {
			currentPlayer.setLane(n, LANE_CA);
			currentPlayer.setIndex(n, newIndex);
		} else if (newIndex == 0 && currentPlayer.getPiece(n).getLane() == NO_LANE) {
			currentPlayer.setLane(n, LANE_BC);
			currentPlayer.setIndex(n, newIndex);
		} else {
			currentPlayer.setIndex(n, newIndex);
			if (newLane) {
				switch(currentPlayer.getPiece(n).getLane()) {
				case NO_LANE:
					currentPlayer.setLane(n, LANE_AB);
					break;
				case LANE_AB:
					currentPlayer.setLane(n, LANE_BC);
					break;
				case LANE_BC:
					currentPlayer.setLane(n, LANE_CD);
					break;
				case LANE_CD:
					currentPlayer.setLane(n, LANE_DA);
					break;
				case LANE_BD:
					currentPlayer.setLane(n, LANE_DA);
					break;
				case LANE_DA:
					currentPlayer.setLane(n, FINISHED);
					break;
				case LANE_CA:
					currentPlayer.setLane(n, FINISHED);
				}
			}
		}
		wonYet();
		System.out.println("Player " + currentPlayer.getNumber() + "'s Piece " + n + " is now in Lane " + currentPlayer.getPiece(n).getLane() + " and index " + currentPlayer.getPiece(n).getIndex());
	}
	
	/**
	 * Check if all pieces held by the current player have the FINISHED lane flag, which means that
	 * they have won and that the game should be terminated.
	 */
	private void wonYet() {
		boolean win = true;
		for (int i = 0; i < 4; i++)
			if (!currentPlayer.wonnered(i)) win = false;
		if (win)
			System.out.println("Player " + currentPlayer.getNumber() + " has won.");
		System.exit(0);
	}
}