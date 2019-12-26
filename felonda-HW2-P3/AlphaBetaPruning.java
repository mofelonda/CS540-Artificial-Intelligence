public class AlphaBetaPruning {
    private double alpha;
    private double beta;
    private int nextMove;
    private double value;
    private int numVisited;
    private int numEvaluated;
    private int maxDepth;
    private boolean maxPlayer;
    private int depthOG;

    public AlphaBetaPruning() {
        this.alpha = Double.NEGATIVE_INFINITY;
        this.beta = Double.POSITIVE_INFINITY;
        this.nextMove = 0;
        this.value = 0;
        this.numVisited = 0;
        this.numEvaluated = 0;
        this.maxDepth = 0;
        this.maxPlayer = true;
    }

    /**
     * This function will print out the information to the terminal,
     * as specified in the homework description.
     */
    public void printStats() {
        System.out.println("Move: " + this.nextMove);
        System.out.println("Value: " + this.value);
        System.out.println("Number of Nodes Visited: " + this.numVisited);
        System.out.println("Number of Nodes Evaluated: " + this.numEvaluated);
        System.out.println("Max Depth Reached: " + this.maxDepth);
        System.out.printf("Avg Effective Branching Factor: %.1f %n",
                (double)(this.numVisited - 1) / (double)(this.numVisited - this.numEvaluated));
    }

    /**
     * This function will start the alpha-beta search
     * @param state This is the current game state
     * @param depth This is the specified search depth
     */
    public void run(GameState state, int depth) {
        this.depthOG = depth;
        this.maxPlayer = state.isMaxPlayer();
        this.value = alphabeta(state, depth, this.alpha, this.beta, this.maxPlayer);
        for (GameState i : state.getSuccessors()) {
            if (i.getValue() == this.value) {
                this.nextMove = i.getLastMove();
                break;
            }
        }
    }

    /**
     * This method is used to implement alpha-beta pruning for both 2 players
     * @param state This is the current game state
     * @param depth Current depth of search
     * @param alpha Current Alpha value
     * @param beta Current Beta value
     * @param maxPlayer True if player is Max Player; Otherwise, false
     * @return int This is the number indicating score of the best next move
     */
    private double alphabeta(GameState state, int depth, double alpha, double beta, boolean maxPlayer) {
        if (maxPlayer) {
            return maxValue(state, depth, alpha, beta);
        }
        else {
            return minValue(state, depth, alpha, beta);
        }
    }

    private double maxValue(GameState state, int depth, double alpha, double beta) {
        this.numVisited++;
        if (depth == 0 || state.isEndGameState()) {
            this.numEvaluated++;
            this.maxDepth = Math.max(this.maxDepth, this.depthOG - depth);
            return state.evaluate(false);
        }
        double tempValue = Double.NEGATIVE_INFINITY;
        for (GameState i : state.setSuccessors()) {
            tempValue = Math.max(tempValue, minValue(i, depth - 1, alpha, beta));
            i.setValue(tempValue);
            if (tempValue >= beta) {
                return tempValue;
            }
            alpha = Math.max(tempValue, alpha);
        }
        return tempValue;
    }

    private double minValue(GameState state, int depth, double alpha, double beta) {
        this.numVisited++;
        if (depth == 0 || state.isEndGameState()) {
            this.numEvaluated++;
            this.maxDepth = Math.max(this.maxDepth, this.depthOG - depth);
            return state.evaluate(true);
        }
        double tempValue = Double.POSITIVE_INFINITY;
        for (GameState i : state.setSuccessors()) {
            tempValue = Math.min(tempValue, maxValue(i, depth - 1, alpha, beta));
            i.setValue(tempValue);
            if (tempValue <= alpha) {
                return tempValue;
            }
            beta = Math.min(tempValue, beta);
        }
        return tempValue;
    }
}
