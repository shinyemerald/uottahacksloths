/**
 * Game piece in the game of yut. Keeps track of its location and its owner.
 */
public class GamePiece {
	private int lane;
	private int index;
	private int player;
	
	public GamePiece(int player) {
		lane = GameOfYut.NO_LANE;
		index = 0;
		this.player = player;
	}
	
	public int getLane() {
		return lane;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getOwner() {
		return player;
	}
	
	public void setLane(int lane) {
		this.lane = lane;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean inHand() {
		return lane == GameOfYut.NO_LANE;
	}
	
	public boolean finished() {
		return lane == GameOfYut.FINISHED;
	}
}
