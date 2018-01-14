import java.awt.geom.Point2D;
import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

/*
 * This method contains the game board, the game logic and its related methods
 */
public class Board extends Observable implements Serializable {

    private Counter[][] gameBoard;
    private List<Point2D> coordinateList;
    private List<Point2D> possibleLegalMovesList = new ArrayList<>();
    private Map<Point2D, ArrayList<String>> legalMoveMap;
    private int whiteCounters;
    private int blackCounters;
    private String currentCounter;
    private byte[] serializedData;
    private int boardSize;

    /*
     * Constructor for the board
     */
    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.gameBoard = new Counter[boardSize][boardSize];
        this.coordinateList = new ArrayList<>();
        initialise();
    }

    public byte[] getSerializedData() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            oos.close();
            bos.close();
            this.serializedData = bos.toByteArray(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedData;
   
    }

    /*
     * Getter for the game board
     */
    public Counter[][] getGameBoard() {
        return gameBoard;
    }

    /*
     * Getter for the board size
     */
    public int getSize() {
        return gameBoard.length;
    }
    /*
     * Stores the location of each counter
     */
    public List<Point2D> getCoordinateList() {
        return coordinateList;
    }

    /*
     * Determines if coordinates on the game board contain a counter of a specific colour
     */
    public boolean hasCounter(String colour, int x, int y) {
        return gameBoard[x][y].getDescription().equals(colour);
    }

    /*
     * Determines if coordinates on the game board contain a non-empty counter
     */
    public boolean hasCounter(int x, int y) {
        return gameBoard[x][y].getDescription().equals("Black") || gameBoard[x][y].getDescription().equals("White");
    }

    /*
     * Getter for the colour of the counter on specified coordinates of the game board
     */
    public String getCounterColour(int x, int y) {
        return gameBoard[x][y].getDescription();
    }

    /*
     * Getter for the current player turn
     */
    public String getCurrentCounter() {
        return currentCounter;
    }

    /*
     * Returns the inverse of a colour
     */
    public String altCounterColour(String counterColour) {
        switch (counterColour) {
            case "Black":
                return "White";
            case "White":
                return "Black";
        }
        return null;
    }

    /*
     * Adds one to the count of a specific Counter colour
     */
    private void incrementCounter(String counterColour) {
        switch (counterColour) {
            case "Black":
                blackCounters++;
                setChanged();
                notifyObservers();
                break;
            case "White":
                whiteCounters++;
                setChanged();
                notifyObservers();
                break;
        }
    }

    /*
     * Subtracts one to the count of a specific Counter colour
     */
    private void decrementCounter(String counterColour) {
        switch (counterColour) {
            case "Black":
                blackCounters--;
                setChanged();
                notifyObservers();
                break;
            case "White":
                whiteCounters--;
                setChanged();
                notifyObservers();
                break;
        }
    }

    /*
     * Returns the count of the amount of black counters
     */
    public int getBlackCount() {
        return blackCounters;
    }

    /*
     * Returns the count of the amount of white counters
     */
    public int getWhiteCount() {
        return whiteCounters;
    }

    /*
     * Returns the count of the amount of counters of a specified colour
     */
    public int getCount(String counterColour) {
        switch (counterColour) {
            case "Black":
                return blackCounters;
            case "White":
                return whiteCounters;
        }
        return -1;
    }

    /*
     * Places a counter object of a specific colour on the game board
     */
    public void addCounter(String colour, int x, int y) {
        switch (colour) {
            case "Black":
                gameBoard[x][y] = new BlackCounter();
                incrementCounter("White");
                coordinateList.add(new Point2D.Double(x, y));
                possibleLegalMovesList.add(new Point2D.Double(x, y));
                break;
            case "White":
                gameBoard[x][y] = new WhiteCounter();
                incrementCounter("Black");
                coordinateList.add(new Point2D.Double(x, y));
                possibleLegalMovesList.add(new Point2D.Double(x, y));
                break;
            default:
                System.out.println("Error: Colour does not exist");
        }
    }

    /*
     * Replaces a counter object on the game board with a specific color
     */
    private void replaceCounter(String colour, int x, int y) {
        switch (colour) {
            case "Black":
                gameBoard[x][y] = new BlackCounter();
                incrementCounter("Black");
                decrementCounter("White");
                break;
            case "White":
                gameBoard[x][y] = new WhiteCounter();
                incrementCounter("White");
                decrementCounter("Black");
                break;
            default:
                System.out.println("Error: Colour does not exist");
        }
    }

    /*
     * Returns the utility score of the counter of a specified colour
     */
    public int getScore(String counterColour) {
        switch (counterColour) {
            case "Black":
                return blackCounters - whiteCounters;
            case "White":
                return whiteCounters - blackCounters;
        }
        return -1;
    }

    /*
     * Places the initial counters on the game board
     */
    private void initialise() {
        for (Counter[] row : gameBoard) {
            Arrays.fill(row, new EmptyCounter());
        }
        addCounter("White", (boardSize/2) - 1, (boardSize/2) - 1);
        addCounter("White", (boardSize/2) , (boardSize/2));
        addCounter("Black", (boardSize/2) - 1, (boardSize/2));
        addCounter("Black", (boardSize/2), (boardSize/2) - 1);
        possibleLegalMovesList = coordinateList;
        initLegalMoves("Black");
    }

    /*
     * Returns a text-based representation of the game board
     */
    public void display() {
        System.out.println("  a b c d e f g h");
        for (int i = 0; i < gameBoard.length; i++) {
            int z = i + 1;
            System.out.print(z + " ");
            for (int j = 0; j < gameBoard[0].length; j++) {
                switch (gameBoard[i][j].getDescription()) {
                    case "Black":
                        System.out.print("B ");
                        break;
                    case "White":
                        System.out.print("W ");
                        break;
                    default:
                        if (legalMoveMap.containsKey(new Point2D.Double(i, j))) {
                            System.out.print("L ");
                        } else {
                            System.out.print("  ");
                        }
                }
            }
            System.out.println();
        }
    }

    /*
     * Adds a legal move coordinate and associated directions to the map
     */
    private void updateLegalMoveMap(Point2D point, String direction) {
        ArrayList<String> directionList = legalMoveMap.get(point);
        if (directionList == null) {
            legalMoveMap.put(point, new ArrayList<>(Collections.singletonList(direction)));
        }
        else {
            directionList.add(direction);
            legalMoveMap.put(point, directionList);
        }
    }

    /*
     * Searches and stores all legal moves
     */
    public void initLegalMoves(String counterColour) {
        currentCounter = counterColour;
        legalMoveMap = new HashMap<>();
        int coordX;
        int coordY;
        List<Point2D> tempList = new ArrayList<>(possibleLegalMovesList);
        for (Point2D point : possibleLegalMovesList) {
            coordX = (int) point.getX();
            coordY = (int) point.getY();
            if (!counterColour.equals(getCounterColour(coordX, coordY))) {
                List<String> directionList = findDirections(point);
                if (directionList.size() == 0) {
                    tempList.remove(point);
                }
                for (String direction : directionList) {
                    switch (direction) {
                        case "North West":
                            if (coordX + 1 < getSize()) {
                                if (coordY + 1 < getSize()) {
                                    int i = 0;
                                    int j = 0;
                                    while (coordX + --i >= 0 && coordY + --j >= 0 && hasCounter(coordX + i, coordY + j)) {
                                        if (hasCounter(counterColour, coordX + i, coordY + j) && !hasCounter(counterColour, coordX + i + 1, coordY + j + 1)) {
                                            updateLegalMoveMap(new Point2D.Double(coordX + 1, coordY + 1), "North West");
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            break;
                        case "North":
                            if (coordX + 1 < getSize()) {
                                int i = 0;
                                while (coordX + --i >= 0 && hasCounter(coordX + i, coordY)) {
                                    if (hasCounter(counterColour, coordX + i, coordY) && !hasCounter(counterColour, coordX + i + 1, coordY)) {
                                        updateLegalMoveMap(new Point2D.Double(coordX + 1, coordY), "North");
                                        break;
                                    }
                                }
                            }
                            break;
                        case "North East":
                            if (coordX + 1 < getSize()) {
                                if (coordY - 1 >= 0) {
                                    int i = 0;
                                    int j = 0;
                                    while (coordX + --i >= 0 && coordY + ++j < getSize() && hasCounter(coordX + i, coordY + j)) {
                                        if (hasCounter(counterColour, coordX + i, coordY + j) && !hasCounter(counterColour, coordX + i + 1, coordY + j - 1)) {
                                            updateLegalMoveMap(new Point2D.Double(coordX + 1, coordY - 1), "North East");
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "West":
                            if (coordY + 1 < getSize()) {
                                int j = 0;
                                while (coordY + --j >= 0 && hasCounter(coordX, coordY + j)) {
                                    if (hasCounter(counterColour, coordX, coordY + j) && !hasCounter(counterColour, coordX, coordY + j + 1)) {
                                        updateLegalMoveMap(new Point2D.Double(coordX, coordY + 1), "West");
                                        break;
                                    }
                                }
                            }
                            break;
                        case "East":
                            if (coordY - 1 >= 0) {
                                int j = 0;
                                while (coordY + ++j < getSize() && hasCounter(coordX, coordY + j)) {
                                    if (hasCounter(counterColour, coordX, coordY + j) && !hasCounter(counterColour, coordX, coordY + j - 1)) {
                                        updateLegalMoveMap(new Point2D.Double(coordX, coordY - 1), "East");
                                        break;
                                    }
                                }
                            }
                            break;
                        case "South West":
                            if (coordX - 1 >= 0) {
                                if (coordY + 1 < getSize()) {
                                    int i = 0;
                                    int j = 0;
                                    while (coordX + ++i < getSize() && coordY + --j >= 0 && hasCounter(coordX + i, coordY + j)) {
                                        if (hasCounter(counterColour, coordX + i, coordY + j) && !hasCounter(counterColour, coordX + i - 1, coordY + j + 1)) {
                                            updateLegalMoveMap(new Point2D.Double(coordX - 1, coordY + 1), "South West");
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "South":
                            if (coordX - 1 >= 0) {
                                int i = 0;
                                while (coordX + ++i < getSize() && hasCounter(coordX + i, coordY)) {
                                    if (hasCounter(counterColour, coordX + i, coordY) && !hasCounter(counterColour, coordX + i - 1, coordY)) {
                                        updateLegalMoveMap(new Point2D.Double(coordX - 1, coordY), "South");
                                        break;
                                    }
                                }
                            }
                            break;
                        case "South East":
                            if (coordX - 1 >= 0) {
                                if (coordY - 1 >= 0) {
                                    int i = 0;
                                    int j = 0;
                                    while (coordX + ++i < getSize() && coordY + ++j < getSize() && hasCounter(coordX + i, coordY + j)) {
                                        if (hasCounter(counterColour, coordX + i, coordY + j) && !hasCounter(counterColour, coordX + i - 1, coordY + j - 1)) {
                                            updateLegalMoveMap(new Point2D.Double(coordX - 1, coordY - 1), "South East");
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        possibleLegalMovesList = tempList;
    }

    /*
     * Determines if the specified coordinates are of a legal move
     */
    public boolean isLegalMove(int x, int y) {
        return legalMoveMap.containsKey(new Point2D.Double(x, y));
    }

    /*
     * Getter for the legal move map
     */
    public Map<Point2D, ArrayList<String>> getLegalMoveMap() {
        return legalMoveMap;
    }

    /*
     * Converts and returns the legal move map in the form of a list
     */
    public List<Point2D> getLegalMoveList() {
        return new ArrayList<>(legalMoveMap.keySet());
    }

    /*
     * Getter for the amount of legal moves
     */
    public int legalMovesPossible() {
        return legalMoveMap.size();
    }

    /*
     * Finds empty neighbours for a specific counter and returns the directions
     * from that counter to the empty neighbours in the form of a List
     */
    private List<String> findDirections(Point2D point) {
        List<String> directionList = new ArrayList<>();
        int xCoord = (int) point.getX();
        int yCoord = (int) point.getY();
        for (int i = xCoord - 1; i <= xCoord + 1; i++) {
            for (int j = yCoord - 1; j <= yCoord + 1; j++) {
                if (i >= 0 && j >= 0 && i < getSize() && j < getSize()) {
                    if (!hasCounter(i, j)) {
                        switch ("(" + (i - xCoord) + ", " + (j - yCoord) + ")") {
                            case "(-1, -1)":
                                directionList.add("South East");
                                break;
                            case "(-1, 0)":
                                directionList.add("South");
                                break;
                            case "(-1, 1)":
                                directionList.add("South West");
                                break;
                            case "(0, -1)":
                                directionList.add("East");
                                break;
                            case "(0, 1)":
                                directionList.add("West");
                                break;
                            case "(1, -1)":
                                directionList.add("North East");
                                break;
                            case "(1, 0)":
                                directionList.add("North");
                                break;
                            case "(1, 1)":
                                directionList.add("North West");
                                break;
                        }
                    }
                }
            }
        }
        return directionList;
    }

    /*
     * Updates the game board once a move is made
     */
    public void update(String counterColour, List<String> directionList, Point2D point) {
        int coordX = (int) point.getX();
        int coordY = (int) point.getY();
        int i;
        int j;
        boolean replaced = false; //to stop the loop
        for (String direction : directionList) {
            switch (direction) {
                case "North West":
                    i = 0;
                    j = 0;
                    while (coordX + --i >= 0 && coordY + --j >= 0 && hasCounter(coordX + i, coordY + j)) {
                        if (!hasCounter(counterColour, coordX + i, coordY + j)) {
                            replaceCounter(counterColour, coordX + i, coordY + j);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
                case "North":
                    i = 0;
                    while (coordX + --i >= 0 && hasCounter(coordX + i, coordY)) {
                        if (!hasCounter(counterColour, coordX + i, coordY)) {
                            replaceCounter(counterColour, coordX + i, coordY);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
                case "North East":
                    i = 0;
                    j = 0;
                    while (coordX + --i >= 0 && coordY + ++j < getSize() && hasCounter(coordX + i, coordY + j)) {
                        if (!hasCounter(counterColour, coordX + i, coordY + j)) {
                            replaceCounter(counterColour, coordX + i, coordY + j);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
                case "West":
                    j = 0;
                    while (coordY + --j >= 0 && hasCounter(coordX, coordY + j)) {
                        if (!hasCounter(counterColour, coordX, coordY + j)) {
                            replaceCounter(counterColour, coordX, coordY + j);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
                case "East":
                    j = 0;
                    while (coordY + ++j < getSize() && hasCounter(coordX, coordY + j)) {
                        if (!hasCounter(counterColour, coordX, coordY + j)) {
                            replaceCounter(counterColour, coordX, coordY + j);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
                case "South West":
                    i = 0;
                    j = 0;
                    while (coordX + ++i < getSize() && coordY + --j >= 0 && hasCounter(coordX + i, coordY + j)) {
                        if (!hasCounter(counterColour, coordX + i, coordY + j)) {
                            replaceCounter(counterColour, coordX + i, coordY + j);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
                case "South":
                    i = 0;
                    while (coordX + ++i < getSize() && hasCounter(coordX + i, coordY)) {
                        if (!hasCounter(counterColour, coordX + i, coordY)) {
                            replaceCounter(counterColour, coordX + i, coordY);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
                case "South East":
                    i = 0;
                    j = 0;
                    while (coordX + ++i < getSize() && coordY + ++j < getSize() && hasCounter(coordX + i, coordY + j)) {
                        if (!hasCounter(counterColour, coordX + i, coordY + j)) {
                            replaceCounter(counterColour, coordX + i, coordY + j);
                            replaced = true;
                        }
                        else if (replaced) {
                            replaced = false;
                            break;
                        }
                    }
                    break;
            }
        }
    }

    /*
     * Makes a move
     */
    public void makeMove(int[] coOrds, String counterColour) {
        int coordX = coOrds[0];
        int coordY = coOrds[1];
        Point2D point;
        point = new Point2D.Double(coordX, coordY);
        this.addCounter(counterColour, coordX, coordY);
        this.update(counterColour, this.getLegalMoveMap().get(point), point);
    }
}
