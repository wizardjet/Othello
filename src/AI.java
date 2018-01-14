import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

public class AI {

    private Board board;
    private String counterColour;
    private String difficulty;
    private int nodesSearched;
    private long startTime;

    /*
     * Constructor for the AI
     */
    public AI(Board board, String counterColour) {

        try {
            PropertiesReader pr = new PropertiesReader();
            this.board = board;
            this.counterColour = counterColour;
            this.difficulty = pr.getAIDifficulty();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error loading difficulty of the AI does your game.properties file exist?", "Can't find game.properties file", JOptionPane.WARNING_MESSAGE);
        }

    }

    private Board restoreStream(byte[] inputStream) {
        Board board = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(inputStream);
            board = (Board) new ObjectInputStream(bais).readObject();
            return board;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return board;
    }

    /*
     * Determines which difficulty level to use
     * Returns the score in the form of a 2D int array
     */
    public int[] getMove() {
        switch (difficulty) {
            case "Novice":
                return getRandomMove();
            case "Adept":
                return getBestMove();
            case "Expert":
                return doMinimax();
            case "Master":
                return doAlphaBeta();
        }
        return getRandomMove();
    }

    /*
     * Returns a random pair of coordinates from the list of legal moves
     * This method is only called in the recursive algorithms
     */
    private int[] getRandomMove(Board currentBoard, String counterColour) {
        currentBoard.initLegalMoves(counterColour);
        List<Point2D> legalMoveList = currentBoard.getLegalMoveList();
        Point2D point = legalMoveList.get(new Random().nextInt(legalMoveList.size()));
        return new int[]{(int) point.getX(), (int) point.getY()};
    }

    /*
     * Returns a random pair of coordinates from the list of legal moves
     */
    public int[] getRandomMove() {
        List<Point2D> legalMoveList = board.getLegalMoveList();
        Point2D point = legalMoveList.get(new Random().nextInt(legalMoveList.size()));
        return new int[]{(int) point.getX(), (int) point.getY()};
    }

    /*
     * Makes a move and returns the game state
     */
    private Board getNextPattern(Board oldBoard, Point2D point, String counterColour) {
        int coordX = (int) point.getX();
        int coordY = (int) point.getY();
        oldBoard.addCounter(counterColour, coordX, coordY);
        oldBoard.update(counterColour, oldBoard.getLegalMoveMap().get(point), point);
        return oldBoard;
    }

    /*
     * Returns the next best move
     */
    private int[] getBestMove() {
        int bestScore = 0;
        int currentScore;
        Point2D bestMove = null;
        Board oldBoard = restoreStream(board.getSerializedData());
        for (Point2D point : board.getLegalMoveList()) {
            Board newBoard = getNextPattern(oldBoard, point, counterColour);
            currentScore = newBoard.getScore(counterColour);
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestMove = point;
            }
        }
        return new int[]{(int) bestMove.getX(), (int) bestMove.getY()};
    }

    /*
     * Performs the traditional minimax algorithm and returns the best move
     */
    public int[] doMinimax() {
        long startTime = System.nanoTime();
        int[] mmx = minimax(board, 4, this.counterColour);
        double timeLapsed = (double) (System.nanoTime() - startTime) * Math.pow(10, -9);
        return new int[]{mmx[1], mmx[2]};
    }

    /*
     * Performs the minimax algorithm with alpha beta pruning and returns the best move
     */
    public int[] doAlphaBeta() {
        System.out.println("Thinking...");
        startTime = System.nanoTime();
        int i = 3;
        double timeLapsed = 0;
        double[] alphabeta = new double[0];
        while (timeLapsed < 4.95 && i < 25) {
            double[] ab = alphaBeta(board, i, this.counterColour, Heuristics.MIN_EVAL, Heuristics.MAX_EVAL);
            timeLapsed = (double) (System.nanoTime() - startTime) * Math.pow(10, -9);
            if (timeLapsed < 4.95) {
                alphabeta = ab;
                i++;
            } else {
                System.out.println("Algorithm terminated at depth " + i);
                System.out.println("Nodes evaluated: " + nodesSearched);
                System.out.println("Nodes per second: " + nodesSearched / getTimeLapsed());
            }
        }
        nodesSearched = 0;
        return new int[]{(int) alphabeta[1], (int) alphabeta[2]};
    }

    /*
     * The traditional minimax algorithm
     */
    private int[] minimax(Board currentBoard, int depth, String counterColour) {
        Board oldBoard = restoreStream(currentBoard.getSerializedData());
        Point2D bestMove = null;
        //terminal nodes, getting utility scores for max
        if (depth == 0 || isOver(oldBoard, counterColour, 2)) {
            int score = oldBoard.getScore(this.counterColour);
            return new int[]{score};
        } else {
            //checking for legal moves for current counter colour
            oldBoard.initLegalMoves(counterColour);
            if (oldBoard.legalMovesPossible() == 0) {
                //switch turn
                int score = minimaxScore(oldBoard, depth, altColour(counterColour));
                return new int[]{score};
            }
            //maximiser
            if (counterColour.equals(this.counterColour)) {
                int score;
                int bestScore = -99;
                for (Point2D coordinate : oldBoard.getLegalMoveList()) {
                    Board newBoard = restoreStream(oldBoard.getSerializedData());
                    newBoard = getNextPattern(newBoard, coordinate, this.counterColour);
                    score = minimaxScore(newBoard, depth, this.counterColour);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = coordinate;
                    }
                }
                if (bestMove != null) {
                    return new int[]{bestScore, (int) bestMove.getX(), (int) bestMove.getY()};
                } else {
                    int[] array = new int[3];
                    array[0] = bestScore;
                    int[] randomMoves = getRandomMove(oldBoard, counterColour);
                    for (int i = 0; i < 2; i++) {
                        array[i + 1] = randomMoves[i];
                    }
                    return array;
                }
            }
            //minimiser
            else {
                int score;
                int bestScore = 99;
                for (Point2D coordinate : oldBoard.getLegalMoveList()) {
                    Board newBoard = restoreStream(oldBoard.getSerializedData());
                    newBoard = getNextPattern(newBoard, coordinate, counterColour);
                    score = minimaxScore(newBoard, depth, counterColour);
                    if (score < bestScore) {
                        bestScore = score;
                        bestMove = coordinate;
                    }
                }
                if (bestMove != null) {
                    return new int[]{bestScore, (int) bestMove.getX(), (int) bestMove.getY()};
                } else {
                    int[] array = new int[3];
                    array[0] = bestScore;
                    int[] randomMoves = getRandomMove(oldBoard, counterColour);
                    for (int i = 0; i < 2; i++) {
                        array[i + 1] = randomMoves[i];
                    }
                    return array;
                }
            }
        }
    }

    /*
     * Minimax algorithm with alpha beta pruning + Dynamic Heuristic Evaluation Function
     */
    private double[] alphaBeta(Board currentBoard, int depth, String counterColour, double α, double β) {
        nodesSearched++;
        Board oldBoard = restoreStream(currentBoard.getSerializedData());
        Point2D bestMove = null;
        //Terminal node, returns the evaluated score
        if (depth == 0 || isOver(oldBoard, counterColour, 2)) {
            double score = Heuristics.evaluate(oldBoard, this.counterColour);
            return new double[]{score};
        } else {
            //Checking for legal moves for current counter colour
            oldBoard.initLegalMoves(counterColour);
            if (oldBoard.legalMovesPossible() == 0) {
                //Switch turn if no legal moves available
                double score = alphaBetaScore(oldBoard, depth, altColour(counterColour), α, β);
                return new double[]{score};
            }
            //Maximiser
            if (counterColour.equals(this.counterColour)) {
                double score;
                for (Point2D coordinate : oldBoard.getLegalMoveList()) {
                    Board newBoard = restoreStream(oldBoard.getSerializedData());
                    newBoard = getNextPattern(newBoard, coordinate, this.counterColour);
                    score = alphaBetaScore(newBoard, depth, this.counterColour, α, β);
                    if (getTimeLapsed() >= 4.95) {
                        break;
                    }
                    if (score > α) {
                        α = score;
                        bestMove = coordinate;
                        if (α >= β) {
                            break;
                        }
                    }
                }
                if (bestMove != null) {
                    return new double[]{α, bestMove.getX(), bestMove.getY()};
                } else {
                    return new double[]{α};
                }
            }
            //Minimiser
            else {
                double score;
                for (Point2D coordinate : oldBoard.getLegalMoveList()) {
                    Board newBoard = restoreStream(oldBoard.getSerializedData());
                    newBoard = getNextPattern(newBoard, coordinate, counterColour);
                    score = alphaBetaScore(newBoard, depth, counterColour, α, β);
                    if (getTimeLapsed() >= 4.95) {
                        break;
                    }
                    if (score < β) {
                        β = score;
                        bestMove = coordinate;
                        if (α >= β) {
                            break;
                        }
                    }
                }
                if (bestMove != null) {
                    return new double[]{β, bestMove.getX(), bestMove.getY()};
                } else {
                    return new double[]{β};
                }
            }
        }
    }

    /*
     * Returns the score for each node for Minimax
     */
    private int minimaxScore(Board currentBoard, int depth, String counterColour) {
        return minimax(currentBoard, depth - 1, altColour(counterColour))[0];
    }

    /*
     * Returns the score for each node for AlphaBeta
     */
    private double alphaBetaScore(Board currentBoard, int depth, String counterColour, double alpha, double beta) {
        return alphaBeta(currentBoard, depth - 1, altColour(counterColour), alpha, beta)[0];
    }

    /*
     * Recursive algorithm to determine if the game is over
     */
    private boolean isOver(Board board, String currentCounter, int inc) {
        Board oldBoard = restoreStream(board.getSerializedData());
        if (oldBoard.legalMovesPossible() == 0) {
            if (oldBoard.getCurrentCounter().equals(altColour(currentCounter))) {
                return true;
            } else if (inc > 1) {
                oldBoard.initLegalMoves(altColour(currentCounter));
                isOver(oldBoard, currentCounter, inc - 1);
            }
        }
        return false;
    }

    /*
     * Inverses the colour specified
     */
    private String altColour(String colour) {
        switch (colour) {
            case "Black":
                return "White";
            case "White":
                return "Black";
        }
        return null;
    }

    /*
     * Gets time lapsed
     */
    private double getTimeLapsed() {
        int base = 10;
        int exp = -9;
        return (System.nanoTime() - startTime) * Math.pow(base, exp);
    }

}
