import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();

		frontier.add(new StateFValuePair(new State(maze.getPlayerSquare(), null, 0, 0),
				0 + getHVal(maze.getPlayerSquare(), maze.getGoalSquare())));

		while (!frontier.isEmpty()) {

			StateFValuePair expandedSFVP = frontier.poll();
			noOfNodesExpanded++;
			cost = expandedSFVP.getState().getGValue();
			//explored[expandedSFVP.getState().getX()][expandedSFVP.getState().getY()] = true;

			ArrayList<State> successors = expandedSFVP.getState().getSuccessors(explored, maze);

			for (State succ : successors) {
				updateSFVP(succ, frontier);
			}

			maxDepthSearched = Math.max(maxDepthSearched, expandedSFVP.getState().getDepth());
			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, frontier.size());

			if (expandedSFVP.getState().isGoal(maze)) {
				State exploredNodeParent = expandedSFVP.getState().getParent();
				while (exploredNodeParent != null && exploredNodeParent.getGValue() != 0) {
					maze.setOneSquare(exploredNodeParent.getSquare(), '.');
					exploredNodeParent = exploredNodeParent.getParent();
				}
				return true;
			}

		}
		return false;
	}

	private int getHVal(Square playerSquare, Square goalSquare) {
		int cost = 0;
		cost = Math.abs(playerSquare.X - goalSquare.X) + Math.abs(playerSquare.Y - goalSquare.Y);
		return cost;
	}

	private void updateSFVP(State succ, PriorityQueue<StateFValuePair> frontier) {
		Object[] fArray = frontier.toArray();
		boolean diff = true;
		for (int i = 0; i < fArray.length; i++) {
			StateFValuePair pair = (StateFValuePair) fArray[i];
			if(pair.getState().getX() == succ.getX() && pair.getState().getY() == succ.getY()){
				diff = false;
				if (succ.getGValue() < pair.getState().getGValue()) {
					StateFValuePair succPair = new StateFValuePair(succ,
							succ.getGValue() + getHVal(succ.getSquare(), maze.getGoalSquare()));
					frontier.remove(pair);
					frontier.add(succPair);
					break;
				}
			}
		}
		if(diff){
			StateFValuePair succPair = new StateFValuePair(succ,
					succ.getGValue() + getHVal(succ.getSquare(), maze.getGoalSquare()));
			frontier.add(succPair);
		}
	}
}
