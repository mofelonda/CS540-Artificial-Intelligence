import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    private int size;            // The number of stones
    private boolean[] stones;    // Game state: true for available stones, false for taken ones
    private int lastMove;        // The last move
    private boolean isMaxPlayer; // Player turn: true for MAX's turn, false for MIN's turn
    private double value;        // Current value
    private List<GameState> successors;

    /**
     * Class constructor specifying the number of stones.
     */
    public GameState(int size, int nTaken) {

        this.size = size;

        //  For convenience, we use 1-based index, and set 0 to be unavailable
        this.stones = new boolean[this.size + 1];
        this.stones[0] = false;

        // Set default state of stones to available
        for (int i = 1; i <= this.size; ++i) {
            this.stones[i] = true;
        }

        // Set the last move be -1
        this.lastMove = -1;

        if (nTaken % 2 == 1) {
            this.isMaxPlayer = false;
        }
        else {
            this.isMaxPlayer = true;
        }
    }

    /**
     * Copy constructor
     */
    public GameState(GameState other) {
        this.size = other.size;
        this.stones = Arrays.copyOf(other.stones, other.stones.length);
        this.lastMove = other.lastMove;
    }


    /**
     * This method is used to compute a list of legal moves
     *
     * @return This is the list of state's moves
     */
    public List<Integer> getMoves() {
        List<Integer> possibleSuccessors;
        int numMoves = 0;
        int idx = 0;
        Integer candidates[];

        if (this.lastMove == -1) {
            for (int i = 1; i < (this.size - 1) / 2 + 1; i++) {
                if (i % 2 == 1) {
                    numMoves++;
                }
            }
            candidates = new Integer[numMoves];
            for (int i = 1; i < (this.size - 1) / 2 + 1; i++) {
                if (i % 2 == 1) {
                    candidates[idx++] = i;
                }
            }
            possibleSuccessors = Arrays.asList(candidates);
        }
        else if (this.lastMove == 1) {
            for (int i = 2; i <= this.size; i++) {
                if (stones[i]) {
                    numMoves++;
                }
            }
            candidates = new Integer[numMoves];
            for (int i = 2; i <= this.size; i++) {
                if (stones[i]) {
                    candidates[idx++] = i;
                }
            }
            possibleSuccessors = Arrays.asList(candidates);
        }
        else {
            for (int i = 1; i < this.lastMove; i++) {
                if (this.lastMove % i == 0 && stones[i] && this.lastMove != i) {
                    numMoves++;
                }
            }
            for (int j = this.size; j > this.lastMove; j--) {
                if (j % this.lastMove == 0 && stones[j] && this.lastMove != j) {
                    numMoves++;
                }
            }
            candidates = new Integer[numMoves];
            for (int i = 1; i < this.lastMove; i++) {
                if (this.lastMove % i == 0 && stones[i] && this.lastMove != i) {
                    candidates[idx++] = i;
                }
            }
            for (int j = this.lastMove; j <= this.size; j++) {
                if (j % this.lastMove == 0 && stones[j] && this.lastMove != j) {
                    candidates[idx++] = j;
                }
            }
            possibleSuccessors = Arrays.asList(candidates);
        }
        return possibleSuccessors;
    }


    /**
     * This method is used to generate a list of successors
     * using the getMoves() method
     *
     * @return This is the list of state's successors
     */
    public List<GameState> setSuccessors() {
        if (this.successors == null) {
            this.successors = this.getMoves().stream().map(move -> {
                var state = new GameState(this);
                state.removeStone(move);
                return state;
            }).collect(Collectors.toList());
        }
        return this.successors;
    }

    public List<GameState> getSuccessors() {
        return this.successors;
    }

    /**
     * This method is used to evaluate a game state based on
     * the given heuristic function
     *
     * @return int This is the static score of given state
     */
    public double evaluate(boolean maxPlayer) {
        if (this.isEndGameState()) {
            if (isMaxPlayer) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else {
            if (this.getStone(1)) {
                return 0;
            }
            else if (this.lastMove == 1) {
                int numMoves = this.getMoves().size();
                if (numMoves % 2 == 1) {
                    if (isMaxPlayer) {
                        return 0.5;
                    }
                    else {
                        return -0.5;
                    }
                }
                else {
                    if (isMaxPlayer) {
                        return -0.5;
                    }
                    else {
                        return 0.5;
                    }
                }
            }
            else if (Helper.isPrime(this.lastMove)) {
                int numMoves = this.getMoves().size();
                if (numMoves % 2 == 1) {
                    if (isMaxPlayer) {
                        return 0.7;
                    }
                    else {
                        return -0.7;
                    }
                }
                else {
                    if (isMaxPlayer) {
                        return -0.7;
                    }
                    else {
                        return 0.7;
                    }
                }
            }
            else if (!Helper.isPrime(this.lastMove)) {
                int count = 0;
                int largestPF = Helper.getLargestPrimeFactor(this.lastMove);
                for (int i = largestPF; i < this.size; i++) {
                    if (i % largestPF == 0 && stones[i]) {
                        count++;
                    }
                }
                if (count % 2 == 1) {
                    if (isMaxPlayer) {
                        return 0.6;
                    }
                    else {
                        return -0.6;
                    }
                }
                else {
                    if (isMaxPlayer) {
                        return -0.6;
                    }
                    else {
                        return 0.6;
                    }
                }
            }
        }
        return 0.0;
    }

    /**
     * This method is used to take a stone out
     *
     * @param idx Index of the taken stone
     */
    public void removeStone(int idx) {
        this.stones[idx] = false;
        this.lastMove = idx;
    }

    /**
     * These are get/set methods for a stone
     *
     * @param idx Index of the taken stone
     */
    public void setStone(int idx) {
        this.stones[idx] = true;
    }

    public boolean getStone(int idx) {
        return this.stones[idx];
    }

    /**
     * These are get/set methods for lastMove variable
     *
     * @param move Index of the taken stone
     */
    public void setLastMove(int move) {
        this.lastMove = move;
    }

    public int getLastMove() {
        return this.lastMove;
    }

    /**
     * This is get method for game size
     *
     * @return int the number of stones
     */
    public int getSize() {
        return this.size;
    }

    /**
     * This determines that the game state is over
     *
     * @return number of moves remaining is 0
     */
    public boolean isEndGameState() {
        return this.getMoves().size() == 0;
    }

    public boolean isMaxPlayer() {
        return isMaxPlayer;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

}	
