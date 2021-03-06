import java.util.Random;
import java.util.ArrayList;

/**
 * Player class for the game of yut. Rolls sticks with RNG and handles its pieces.
 */
public class Player {
	private int playerNumber;
	private ArrayList<Integer> rolls;
	private GamePiece[] pieces;
	
	public Player(int player) {
		playerNumber = player;
		rolls = new ArrayList<Integer>();
		pieces = new GamePiece[4];
		for (int i = 0; i < 4; i++)
			pieces[i] = new GamePiece(getNumber());
	}
	
	public int roll() {
		Random rng = new Random();
		int count = 0;
		for (int i = 0; i < 4; i++)
			if (rng.nextInt(2) == 1) count++;
		if (count == 0)
			count = 5;
		rolls.add(count);
		return count;
	}
	
	public int useRoll(int moves) {
		int output = rolls.remove(rolls.indexOf(moves));
		return output;
	}
	
	public int numRolls() {
		return rolls.size();
	}
	
	public String getStringRolls() {
		String output = "";
		for (int i = 0; i < numRolls(); i++)
			output += rolls.get(i) + " ";
		return output;
	}
	
	public int getNumber() {
		return playerNumber;
	}
	
	public GamePiece getPiece(int index) {
		return pieces[index];
	}
	
	public ArrayList<GamePiece> getPieces() {
		ArrayList<GamePiece> pieces = new ArrayList<GamePiece>();
		for (int i = 0; i < 4; i++)
			pieces.add(this.pieces[i]);
		return pieces;
	}
	
	public void setIndex(int i, int j) {
		pieces[i].setIndex(j);
	}
	
	public void setLane(int i, int j) {
		pieces[i].setLane(j);
	}
	
	public boolean wonnered(int index) {
		return pieces[index].getLane() == GameOfYut.FINISHED;
	}
}
