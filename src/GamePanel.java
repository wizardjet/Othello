/**
 * This class is a JPanel which displays the game and has methods 
 * to allow the user to click on the board in order to make a 
 * move.
 * It implements the MouseListener interface to determine if a user has clicked
 * and finds what the user has clicked.
 */

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class GamePanel extends JPanel implements MouseListener {

    private Board board;
    private int boardSize = 0;
    private String counterColour = "Black"; //black starts first
    private boolean singlePlayer;
    private boolean networkMultiplayer;
    private boolean playersMove = true;

    /**
     * Constructor for the GamePanel determines who plays first depending on
     * what the user chooses.
     */
    public GamePanel(boolean singlePlayer, boolean networkMultiplayer, String counterColour) {
        //setting GamePanel variables
        this.singlePlayer = singlePlayer;
        this.networkMultiplayer = networkMultiplayer;
        this.counterColour = counterColour;
        boardSize = setBoardSize();

        //setting up the panel and making it visible
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setLayout(new GridLayout(boardSize, boardSize));

        
        //creating a new board
        this.board = new Board(boardSize);

        //display the JLabels
        addJLabels();
        setVisible(true);
        
        //depending on the user choice for singleplayer, determine who moves first
        if (singlePlayer) {
            if (counterColour.equals("White")) {
                this.counterColour = "Black";
                aiMakeMove();
            }
        }

    }

    public void setBoard(Board newBoard) {
        this.board = newBoard;
        displayBoard();
        //setting up the panel and making it visible
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        setLayout(new GridLayout(boardSize, boardSize));
        //display the JLabels
        setVisible(true);
    }

    /**
     * Method to get the board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Method which sets the board size
     * @return integer which is the board size
     */
    private int setBoardSize() {
        try {
            PropertiesReader pr = new PropertiesReader();
            return Integer.parseInt(pr.getBoardsize());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error setting board size, has properties file been set?", "Can't set board size", JOptionPane.WARNING_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error setting board size, does your game.properties file exist?", "Can't find game.properties file", JOptionPane.WARNING_MESSAGE);
        }
        //return default size if there's been an error
        return 8;
    }

    /**
     * This method adds individual JLabels to the panel
     */
    private void addJLabels() {
        //declare a new GameTile object
        GameTile gt;

        //The game board as a counter array
        Counter[][] gameBoard = board.getGameBoard();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {

                //if the gameboard is empty at that position
                if (gameBoard[i][j].getDescription().equals("Empty") ) {

                    //check if it is a legal move
                    if (board.isLegalMove(i, j)) {
                        //position of the game tile
                        int[] position = {i , j};
                        gt = new GameTile(position);
                        //set the tile to represent a legal move tile
                        gt.setLegal();
                        this.add(gt);
                        //add mouse listener to the tile so the program knows when the user clicks it
                        gt.addMouseListener(this);

                    //if it is not a legal move then its just empty
                    } else {
                        //position of the game tile
                        int[] position = {i , j};
                        gt = new GameTile(position);
                        //set the tile to represent an empty tile
                        gt.setEmpty();
                        this.add(gt);
                        //add mouse listener to the tile so the program knows when the user clicks it
                        gt.addMouseListener(this);
                    }

                //if the gmeboard contains a white counter at that position
                } else if (gameBoard[i][j].getDescription().equals("White")) {
                    //position of the game tile
                    int[] position = {i , j};
                    gt = new GameTile(position);
                    //set the game tile to represent a white counter
                    gt.setWhite();
                    this.add(gt);
                    //add mouse listener to the tile so the program knows when the user clicks it
                    gt.addMouseListener(this);

                //if the gameboard contains a black counter at that position
                } else if (gameBoard[i][j].getDescription().equals("Black")) {
                    //position of the game tile
                    int[] position = {i , j};
                    gt = new GameTile(position);
                    //set the game tile to represent a black counter
                    gt.setBlack();
                    this.add(gt);
                    //add mouse listener to the tile so the program knows when the user clicks it
                    gt.addMouseListener(this);
                }
            }
        }
    }

    /**
     * Once a player or the AI has made a move, remove all the components of the
     * panel and redraw it. This makes sure that the user sees accurate information 
     * every move
     */
    public void displayBoard() {
        this.removeAll();
        addJLabels();
        this.revalidate();
        this.repaint();
    }

    /**
     * This method is triggered when the user clicks their mouse on the JPanel
     * @param the mouse event that triggeres the method
     */
    public void mouseClicked(MouseEvent e) {

        //single player playing with AI
        if (singlePlayer) {
            playSinglePlayer(e);
        //local multiplayer
        } else if (!singlePlayer && !networkMultiplayer) {
            playLocalMultiplayer(e);
        }
    }

    /**
     * Method which plays the local multiplayer game
     */
    private void playLocalMultiplayer(MouseEvent e) {
        //initialise all legal moves for the current counter colour
            board.initLegalMoves(counterColour);

            //check if the player has legal moves
            if (board.legalMovesPossible() > 0) {

                //display the board
                displayBoard();
                //find the position of the tile the user clicked on
                int[] coOrds = ((GameTile) e.getSource()).getPosition();

                //verify the tile the user clicked on is a legal move
                if (board.isLegalMove(coOrds[0], coOrds[1])) {

                    //make the specified move
                    board.makeMove(coOrds, counterColour);
                    //display the board
                    displayBoard();
                    //switch the counter colour
                    counterColour = board.altCounterColour(counterColour);
                    //initialise the legal moves for the next player
                    board.initLegalMoves(counterColour);
                    //display the board
                    displayBoard();

                    //if the next player does not have any legal moves, display a message
                    if (board.legalMovesPossible() == 0) {
                        displayNoLegalMoveMessage();
                    }

                //user clicks on a position that is not a legal move
                } else {
                    JOptionPane.showMessageDialog(null, "That is not a legal move.", "Not a legal move!", JOptionPane.WARNING_MESSAGE);
                }

            //user has no legal moves to make
            } else {
                displayNoLegalMoveMessage();
            }
    }

    /**
     * Method which plays singleplayer game
     */
    private void playSinglePlayer(MouseEvent e) {
        
        //player's move
        if (playersMove) {

            //initialise all legal moves for the current counter colour
            board.initLegalMoves(counterColour);
            if (board.legalMovesPossible() > 0) {

                //find the coOrds of the JLabel the user clicked on
                int[] coOrds = ((GameTile) e.getSource()).getPosition();

                //verify that the user has clicked on a legal position
                if (board.isLegalMove(coOrds[0], coOrds[1])) {

                    //make the specified move
                    board.makeMove(coOrds, counterColour);
                    //display the board
                    displayBoard();
                    //flip the variable so that it is not the player's move
                    playersMove = false;
                    //change the counter colour
                    counterColour = board.altCounterColour(counterColour);
                    //display legal move for the AI
                    board.initLegalMoves(counterColour);
                    displayBoard();
                    //simulate a mouse click for the AI to run
                    simulateMouseClick();

                //user does not click on a legal position  
                } else {
                    JOptionPane.showMessageDialog(null, "That is not a legal move.", "Not a legal move!", JOptionPane.WARNING_MESSAGE);
                    playersMove = true;
                }

            //user has no legal moves to make
            } else {
                displayNoLegalMoveMessage();
                playersMove = false;
                aiMakeMove();
            }

        //else its the ai's turn to move
        } else {
            aiMakeMove();
        }
    }

    /**
     * This method displays a message which states that the current player has no legal moves
     * The method then continues on to check if the next player has legal moves, if no, the game
     * ends with the final scores displayed.
     */
    public void displayNoLegalMoveMessage() {
        JOptionPane.showMessageDialog(null, counterColour + " has no legal moves.", "No legal moves!", JOptionPane.WARNING_MESSAGE);
        //switch to opposite colour
        counterColour = board.altCounterColour(counterColour);
        //check the legal moves for the oppoiste colour
        board.initLegalMoves(counterColour);
        //if opposite colour does not have any legal moves, display end game message
        if (board.legalMovesPossible() == 0) {
            JOptionPane.showMessageDialog(null, counterColour + " has no legal moves.", "No legal moves!", JOptionPane.WARNING_MESSAGE);
            displayEndGameMessage();
        } else {
            //check for singleplayer game vs AI
            if(singlePlayer && playersMove) {
                displayBoard();
            } else {
                aiMakeMove();
            }
        }
    }

    /**
     * This method initialises a new AI and makes a move on the current board
     */
    private void aiMakeMove() {
        AI ai = new AI(board, counterColour);
        if (board.legalMovesPossible() > 0) {
            board.makeMove(ai.getMove(), counterColour);
            counterColour = board.altCounterColour(counterColour);
            //check player has legal moves for autodetect game end
            board.initLegalMoves(counterColour);
            displayBoard();
            if (board.legalMovesPossible() == 0) {
                displayNoLegalMoveMessage();
                playersMove = false;
            } else {
                playersMove = true;
            }
        } else {
            displayNoLegalMoveMessage();
            playersMove = true;
        }
    }

    /**
     * This method displays an end game message to the user detailing who won.
     * Once the JOptionPane has been terminated, the ancestor frame of the GamePanel,
     * GameFrame, is closed.
     */
    public void displayEndGameMessage() {

        //calculate scores
        int noOfBlackCounters = board.getBlackCount();
        int noOfWhiteCounters = board.getWhiteCount();

        //display message
        if (noOfBlackCounters > noOfWhiteCounters) {
            JOptionPane.showMessageDialog(null, "Black has won " + noOfBlackCounters + ":" + noOfWhiteCounters, "Black Wins!", JOptionPane.INFORMATION_MESSAGE);
            //close the game panel
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
        } else if (noOfWhiteCounters > noOfBlackCounters) {
            JOptionPane.showMessageDialog(null, "White has won " + noOfWhiteCounters + ":" + noOfBlackCounters, "White Wins!", JOptionPane.INFORMATION_MESSAGE);
            //close the game panel
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
        } else {
            JOptionPane.showMessageDialog(null, "It's a tie game! ", "Tie Game!", JOptionPane.INFORMATION_MESSAGE);
            //close the game panel
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
        }

    }

    /**
     * This method simulates a mouse click based on where the users mouse is, 
     * this code was adapted from stackoverflow: 
     * http://stackoverflow.com/questions/19185162/how-to-simulate-a-real-mouse-click-using-java
     */
    private void simulateMouseClick() {
        try {
            Robot rb = new Robot();
            PointerInfo a = MouseInfo.getPointerInfo();
            Point b = a.getLocation();
            int x = (int) b.getX();
            int y = (int) b.getY();
            rb.mouseMove(x, y);
            rb.mousePress(InputEvent.BUTTON1_MASK);
            rb.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException evt) {
            evt.printStackTrace();
        }
    }


    /**
     * These are mouse listener interface methods that this programme did not use
     * but they could be utilised for further expandabilitiy.
     */
    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
