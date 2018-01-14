/**
 * This class is a utilities class that provides common methods between
 * OthelloClient and OthelloServer. 
 * It is a Singleton class.
 */

import java.util.Scanner;

public class OthelloNetworkUtilities {

    //static 
    private final static int PASS_MOVE = 1;
    private final static int WHITE_WINS = 2;
    private final static int BLACK_WINS = 3;
    private final static int TIE_GAME = 4;

    //get an instance of the class
    private static OthelloNetworkUtilities utilities = new OthelloNetworkUtilities();

    /**
     * Private constructor 
     */
    private OthelloNetworkUtilities() { }

    /**
     * Method to get an instance of the class
     */
    public static OthelloNetworkUtilities getInstance() {
        return utilities;
    }

    /**
     * Method to translate received input to int array the board understands
     * @param the command received from either side
     * @return co ordinates the board class can understand
     */
    public static int[] translateInput(String command) throws MoveNotFoundException {
        int[] coOrds = new int[2];

        if (command.length() != 7) {
            throw new MoveNotFoundException();
        } else {
            command = command.substring(5);
        }

        String[] letterArray = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] numericalArray = {"1", "2", "3", "4", "5", "6", "7", "8"};

        for (int i = 0; i < letterArray.length; i++) {
            for (int j = 0; j < numericalArray.length; j++) {
                if (command.equals(letterArray[i] + numericalArray[j])) {
                    coOrds[0] = j;
                    coOrds[1] = i;
                }
            }
        }

        if (coOrds == null) {
            throw new MoveNotFoundException();
        }
        return coOrds;
    }

    /**
     * Method to translate board output to string the network protocol specifies
     * @param the co ordinates the board plays
     * @return string the protocol specifies
     */
    public static String translateOutput(int[] coOrds) throws MoveNotFoundException {

        String translatedString = "";

        String[] letterArray = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] numericalArray = {"1", "2", "3", "4", "5", "6", "7", "8"};

        for (int i = 0; i < numericalArray.length; i++) {
            for (int j = 0; j < letterArray.length; j++) {
                if (i == coOrds[0] && j == coOrds[1]) {
                    translatedString = letterArray[j] + numericalArray[i];
                }
            }
        }

        if (translatedString.equals("")) {
            throw new MoveNotFoundException();
        }

        return translatedString;
    }

    /**
     * Method to check whether the received input is in valid format or not
     * @param the move received from client/server
     * @return whether the move is valid or not
     */
    public static boolean isValid(String move) throws MoveNotFoundException {
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8"};

        if (move.length() != 7) {
            throw new MoveNotFoundException();
        } else {
            move = move.substring(5);
        }

        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < numbers.length; j++) {
                if (move.equals(letters[i]+numbers[j])) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Method which checks if after a player has no legal moves, if the game has ended
     * or not.
     * @param currentCounterColour - the current counter colour
     * @param board - the current board
     * @return an integer which represents one of four states; pass, tie, white win, black win
     */
    public static int checkEndGame(String currentCounterColour, Board board) {

        System.out.println("You have no legal moves");

        //check if opponent has legal moves
        board.initLegalMoves(board.altCounterColour(currentCounterColour));
        if (board.legalMovesPossible() > 0) {
            return PASS_MOVE;
        } else {
            System.out.println("Opponent has no legal moves");
            //tie game
            if (board.getWhiteCount() == board.getBlackCount()) {
                System.out.println("Result: Tie game");
                board.display();
                return TIE_GAME;

            //white wins
            } else if (board.getWhiteCount() > board.getBlackCount()) {
                System.out.println("Result: Server has won the game");
                System.out.println("White: " + board.getWhiteCount() + "     Black: " + board.getBlackCount());
                return WHITE_WINS;

            //black wins
            } else {
                System.out.println("Result: Clieht has won the game");
                System.out.println("White: " + board.getWhiteCount() + "     Black: " + board.getBlackCount());
                return BLACK_WINS;
            }
        }
    }

    /**
     * Method which makes the AI move on the network
     * @param AI - the AI
     * @param board - the current board on the network
     * @param counterColour - the current counter colour / the colour the AI plays
     * @return String of the command sent to either client or server
     */
    public static String makeMoveAI(AI ai, Board board, String counterColour) {
        String reply = "";

        //set ai onto the current board
        board.initLegalMoves(counterColour);
        if (board.legalMovesPossible() > 0) {
            try {
                int[] coOrds = ai.getMove();
                String move = translateOutput(coOrds);
                reply = "move " + move;
            } catch (MoveNotFoundException e) {
                System.out.println("An error occurred with the AI");
            }

        //check for end game / win condition
        } else {
            int outcome = checkEndGame(counterColour, board);
            switch (outcome) {
                case PASS_MOVE:
                    reply = "move pass";
                    break;
                case WHITE_WINS:
                    reply = "White win";
                    break;
                case BLACK_WINS:
                    reply = "Black win";
                    break;
                case TIE_GAME:
                    reply = "we tie";
                    break;
                default:
                    break;
            }
        }

        return reply;
    }

    /**
     * Method which allows the human to decide a move on the current board
     * @param board - the current board
     * @param - the current counter colour / the colour the Human plays
     * @return String of the command sent to either client or server
     */
    public static String makeMoveHuman(Board board, String counterColour) {

        int resendCounter = 0;
        String command = "";
        Scanner sc = new Scanner(System.in);

        //initialise the legal moves of the board and display it
        board.initLegalMoves(counterColour);
        board.display();

        //client has legal moves
        if (board.legalMovesPossible() > 0) {
            while (true) {
                System.out.print("Enter a move: ");
                command = sc.nextLine();
                try {
                    if (isValid(command)) {
                        int[] coOrds = translateInput(command);
                        if (board.isLegalMove(coOrds[0], coOrds[1])) {

                            //user inputs valid move
                            return command;

                        //user inputs invalid move
                        } else {
                            throw new MoveNotFoundException();
                        }

                    //user inputs invalid command
                    } else {
                        throw new MoveNotFoundException();
                    }
                } catch (MoveNotFoundException e) {
                    //other commands that aren't a move
                    if (command.equals("bye") || command.equals("You win") || command.equals("I win")) {
                        return command;
                    //resend counter if client sends more than 2 unknown commands
                    } else if (resendCounter < 2) {
                        System.out.println("Not a legal move, please try again, mistake counter: " + (resendCounter + 1));
                        resendCounter++;
                    } else {
                        System.out.println("You have sent 2 commands in succession which do not make sense, server wins by default");
                        command = "You win";
                        return command;
                    }
                }
            }

        //check for end game / win condition
        } else {
            int outcome = checkEndGame(counterColour, board);
            switch (outcome) {
                case PASS_MOVE:
                    command = "move pass";
                    break;
                case WHITE_WINS:
                    command = "White win";
                    break;
                case BLACK_WINS:
                    command = "Black win";
                    break;
                case TIE_GAME:
                    command = "we tie";
                    break;
                default:
                    break;
            }
        }

        return command;
    }

}
