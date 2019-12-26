import java.util.ArrayList;

/**
 * A state in the search represented by the (x,y) coordinates of the square and
 * the parent. In other words a (square,parent) pair where square is a Square,
 * parent is a State.
 * 
 * You should fill the getSuccessors(...) method of this class.
 * 
 */
public class State {

	private Square square;
	private State parent;

	// Maintain the gValue (the distance from start)
	// You may not need it for the BFS but you will
	// definitely need it for AStar
	private int gValue;

	// States are nodes in the search tree, therefore each has a depth.
	private int depth;

	/**
	 * @param square
	 *            current square
	 * @param parent
	 *            parent state
	 * @param gValue
	 *            total distance from start
	 */
	public State(Square square, State parent, int gValue, int depth) {
		this.square = square;
		this.parent = parent;
		this.gValue = gValue;
		this.depth = depth;
	}

	/**
	 * @param visited
	 *            explored[i][j] is true if (i,j) is already explored
	 * @param maze
	 *            initial maze to get find the neighbors
	 * @return all the successors of the current state
	 */
	public ArrayList<State> getSuccessors(boolean[][] explored, Maze maze) {

		ArrayList<State> unvisitedSuccessors = new ArrayList<State>();

		// Left; if y = 0, there cannot be a state to the left of the current position
		if (getY() != 0) {
			if (maze.getSquareValue(getX(), getY() - 1) != '%' && explored[getX()][getY() - 1] == false) {
				State unvisitedLeft = new State(new Square(getX(), getY() - 1), this,getGValue() + 1, getDepth() + 1);
				unvisitedSuccessors.add(unvisitedLeft);
				explored[getX()][getY()-1] = true;
			}
		}

		// Down; if x = the total # of rows (excluding the border), there cannot be a state below the current position
		if (getX() != maze.getNoOfRows() - 1) {
			if (maze.getSquareValue(getX() + 1, getY()) != '%' && explored[getX() + 1][getY()] == false) {
				State unvisitedDown = new State(new Square(getX() + 1, getY()), this, getGValue() + 1, getDepth() + 1);
				unvisitedSuccessors.add(unvisitedDown);
				explored[getX()+1][getY()] = true;
			}
		}

		// Right; if y = the total # of cols (excluding the border), there cannot be a state to the right of the current position
		if (getY() != maze.getNoOfCols() - 1) {
			if (maze.getSquareValue(getX(), getY() + 1) != '%' && explored[getX()][getY() + 1] == false) {
				State unvisitedRight = new State(new Square(getX(), getY() + 1), this, getGValue() + 1, getDepth() + 1);
				unvisitedSuccessors.add(unvisitedRight);
				explored[getX()][getY()+1] = true;
			}
		}

		// Up; if x = 0, there cannot be a state above the current position
		if (getX() != 0) {
			if (maze.getSquareValue(getX() - 1, getY()) != '%' && explored[getX() - 1][getY()] == false) {
				State unvisitedUp = new State(new Square(getX() - 1, getY()), this, getGValue() + 1, getDepth() + 1);
				unvisitedSuccessors.add(unvisitedUp);
				explored[getX()-1][getY()] = true;
			}
		}

		return unvisitedSuccessors;
	}

	/**
	 * @return x coordinate of the current state
	 */
	public int getX() {
		return square.X;
	}

	/**
	 * @return y coordinate of the current state
	 */
	public int getY() {
		return square.Y;
	}

	/**
	 * @param maze initial maze
	 * @return true is the current state is a goal state
	 */
	public boolean isGoal(Maze maze) {
		if (square.X == maze.getGoalSquare().X
				&& square.Y == maze.getGoalSquare().Y)
			return true;

		return false;
	}

	/**
	 * @return the current state's square representation
	 */
	public Square getSquare() {
		return square;
	}

	/**
	 * @return parent of the current state
	 */
	public State getParent() {
		return parent;
	}

	/**
	 * You may not need g() value in the BFS but you will need it in A-star
	 * search.
	 * 
	 * @return g() value of the current state
	 */
	public int getGValue() {
		return gValue;
	}

	/**
	 * @return depth of the state (node)
	 */
	public int getDepth() {
		return depth;
	}
}
