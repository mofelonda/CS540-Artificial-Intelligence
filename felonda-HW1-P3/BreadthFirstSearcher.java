import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Breadth-First Search (BFS)
 * 
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public BreadthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main breadth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// explored list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// Queue implementing the Frontier list
		LinkedList<State> queue = new LinkedList<State>();

		State playerStart = new State(maze.getPlayerSquare(), null, 0, 0);
		queue.add(playerStart);
		maxSizeOfFrontier = Math.max(queue.size(), maxSizeOfFrontier);

		while (!queue.isEmpty()) {
			State exploredNode = queue.pop();
			noOfNodesExpanded++;
			explored[exploredNode.getX()][exploredNode.getY()] = true;
			maxDepthSearched = Math.max(exploredNode.getDepth(), maxDepthSearched);

			if (exploredNode.isGoal(maze)) {
				State exploredNodeParent = exploredNode.getParent();
				cost++;
				while (exploredNodeParent != null && exploredNodeParent.getGValue() != 0) {
					maze.setOneSquare(exploredNodeParent.getSquare(), '.');
					exploredNodeParent = exploredNodeParent.getParent();
					cost++;
				}
				return true;
			}

			ArrayList<State> successors = new ArrayList<State>();
			successors = exploredNode.getSuccessors(explored, maze);

			//if (queue.isEmpty()) {
			for (State succ : successors) {
				queue.add(succ);
			}
			maxSizeOfFrontier = Math.max(queue.size(), maxSizeOfFrontier);
			//}
			//else {
//				for (int s = 0; s < successors.size(); s++) {
//					boolean diff = true;
//					for (int i = 0; i < queue.size(); i++) {
//						if (successors.get(s).getX() == queue.get(i).getX() &&
//								successors.get(s).getY() == queue.get(i).getX()) {
//							diff = false;
//						}
//						else {
//							//continue;
//						}
//					}
//					if (diff) {
//						queue.add(successors.get(s));
//					}
//				}
			//}
				//System.out.println(queue);

		}
		System.out.println("cost: " + cost);
		System.out.println("nodes expanded: " + noOfNodesExpanded);
		System.out.println("max depth: " + maxDepthSearched);
		System.out.println("max size of frontier: " + maxSizeOfFrontier);
		return false;
	}
}
