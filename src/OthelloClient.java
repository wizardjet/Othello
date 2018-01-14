/**
 * This class is the client for the network play of Othello.
 */

import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import java.util.Scanner;

public class OthelloClient {

    private Board board;
    private BufferedReader serverInput;
    private PrintWriter clientOutput;
    private Socket client;
    private OthelloNetworkUtilities networkUtilities = OthelloNetworkUtilities.getInstance();

    private boolean isAI;
    private String counterColour = "Black";
    private String command = "";
    private String reply = "";

    /**
     * Constructor for the Client
     * @param the GameType - could be AI or Human - what the client wants to play
     */
    public OthelloClient(String gameType, Board board) {

        this.board = board;

        try {

            //code adapted from the example on studres: https://studres.cs.st-andrews.ac.uk/CS1006/Examples/NCClient.java
            String hostname = JOptionPane.showInputDialog(null, "Enter ip address of server");

            // Connects to a server
            Socket server = new Socket(hostname, 10006);

            if (gameType.equals("AI")) {
                this.isAI = true;
                server.setSoTimeout(5000);
            } else {
                this.isAI = false;
                server.setSoTimeout(30000);
            }

            System.out.println("Server accepted connection");
            //reading from server
            serverInput = new BufferedReader(new InputStreamReader(server.getInputStream()));

            // Sending to server
            clientOutput = new PrintWriter(server.getOutputStream(), true);

            //intialise game erquest / game play
            initGamePlay();

            //close the server
            server.close();

        } catch (UnknownHostException e) {
            System.out.println("Unkown host, does the host exist?");
        } catch (IOException e){
            System.out.println("IO Error");
        } catch (NullPointerException e) {
            System.out.println("Server did not respond");
        }
    }

    //initialising game play over the network
    private void initGamePlay() throws IOException, SocketException {
        Scanner sc = new Scanner(System.in);
        String userInput;

        //request loop
        do {
            System.out.print("\nSend game request to server: ");
            userInput = sc.nextLine();
            clientOutput.println(userInput);
            clientOutput.flush();
            String reply = serverInput.readLine();

            //hello
            if (reply.equals("hello")) {
                System.out.println("Server replied with " + reply);

            //accept
            } else if (reply.equals("accept")) {
                //start game
                runGame();
                userInput = "quit";

            //reject
            } else if (reply.equals("reject")) {
                userInput = "quit";

            //if none of the cases are satisfied
            } else {
                System.out.print("Server has replied with: " + reply);
            }

        } while (!userInput.equals("quit"));

        System.out.println("Client connection shutting down");
        serverInput.close();
        clientOutput.close();

    }

    //method for gameplay on the network
    private void runGame() {

        int resendCounter = 0;

        do {
            try {

                //client playing
                if(counterColour.equals("Black")) {

                    //AI playing
                    if (isAI) {
                        AI ai = new AI(board, counterColour);
                        String reply = networkUtilities.makeMoveAI(ai, board, counterColour);
                        sendReply(reply);

                    } else {
                        //human playing
                        String reply = networkUtilities.makeMoveHuman(board, counterColour);
                        board.display();
                        sendReply(reply);
                    }

                //server response
                } else {

                    //server reply
                    reply = serverInput.readLine();

                    //server sends resend
                    if (reply.equals("resend")) {
                        clientOutput.println(command);
                        clientOutput.flush();

                    //server sends win message
                    } else if (reply.equals("You win")) {
                        System.out.println("White: " + board.getWhiteCount() + "     Black: " + board.getBlackCount());
                        System.out.println("Game over, client wins");
                        command = "bye";

                    //server sends bye message
                    } else if (reply.equals("bye")) {
                        command = "bye";

                    //server sends pass message
                    } else if (reply.equals("move pass")) {
                        counterColour = board.altCounterColour(counterColour);

                    //verify that the server wins
                    } else if (reply.equals("I win")) {
                        System.out.println("White: " + board.getWhiteCount() + "     Black: " + board.getBlackCount());
                        if (board.getWhiteCount() > board.getBlackCount()) {
                            command = "bye";
                        } else {
                            System.out.println("White counters are not greater than black counters, please review the board");
                            board.display();
                            command = "bye";
                        }

                    //tie game
                    } else if (reply.equals("we tie")) {
                        board.display();
                        command = "bye";

                    //server sends 'move' eg 'move e3' or other
                    } else {

                        try {
                            //obtain co ords of server move
                            int[] coOrds = networkUtilities.translateInput(reply);

                            //translate co ords to board system
                            String move = networkUtilities.translateOutput(coOrds);

                            //check if move is legal
                            board.initLegalMoves(counterColour);
                            if (board.isLegalMove(coOrds[0], coOrds[1])) {
                                //output move and update the board
                                System.out.println("Server moved: " + move);
                                board.makeMove(coOrds, counterColour);
                                resendCounter = 0; //reset the counter after every legal move
                                counterColour = board.altCounterColour(counterColour);

                            //server sends an illegal move
                            } else {
                                clientOutput.println("I win");
                                clientOutput.flush();
                                command = "bye";
                            }

                        //server sends a move the server doesn't understand
                        } catch (MoveNotFoundException e ) {
                            //server can't send more than 2 messages the client does not understand
                            if (resendCounter < 2) {
                                clientOutput.println("resend");
                                clientOutput.flush();
                                resendCounter++;
                            } else {
                                System.out.println("Server sent 2 commands in succession which do not make sense, client wins by default");
                                clientOutput.println("I win");
                                clientOutput.flush();
                                command = "bye";
                            }
                        }
                    }
                }
            } catch (IOException e ) {
                System.out.println("Server took too long to respond!!");
                System.out.println("Client wins by default.");
                // close the connection
                // System.out.println("Client connection shutting down");
                // clientOutput.println("I win");
                // clientOutput.flush();
                // clientOutput.close();
            } catch (MoveNotFoundException e) {
                System.out.println("An error occurred with the client");
            }

        } while (!command.equals("bye"));
    }

    //send the reply to the server
    private void sendReply(String reply) throws MoveNotFoundException {
        
        //white win
        if (reply.equals("White win")) {
            reply = "You win";
            command = "bye";
            clientOutput.println(reply);
            clientOutput.flush();

        //black win
        } else if (reply.equals("Black win")) {
            reply = "I win";
            command = "bye";
            clientOutput.println(reply);
            clientOutput.flush();

        //we tie
        } else if (reply.equals("we tie")) {
            clientOutput.println("we tie");
            command = "bye";
            clientOutput.flush();

        //move pass
        } else if (reply.equals("move pass")) {
            clientOutput.println(reply);
            clientOutput.flush();
            counterColour = board.altCounterColour(counterColour);

        //You win
        } else if (reply.equals("You win")) {
            command = "bye";
            clientOutput.println(reply);
            clientOutput.flush();

        //move
        } else {
            System.out.println(reply);
            int coOrds[] = networkUtilities.translateInput(reply);
            System.out.println("Client moved: " + networkUtilities.translateOutput(coOrds));
            board.makeMove(coOrds, counterColour);
            counterColour = board.altCounterColour(counterColour);
            clientOutput.println(reply);
            clientOutput.flush();
        }
    }
}
