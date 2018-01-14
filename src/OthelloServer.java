/**
 * This class is the server for the network play of Othello.
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class OthelloServer {

    
    private BufferedReader clientInput;
    private PrintWriter serverOutput;
    private Socket client;
    private Scanner sc = new Scanner(System.in);

    private OthelloNetworkUtilities networkUtilities = OthelloNetworkUtilities.getInstance();
    private boolean isAI;
    private Board board;
    private String counterColour = "Black";
    private String command = "";
    private String reply = "";

    //constructor for the server
    public OthelloServer(String gameType, Board board) {

        this.board = board;

        try {

            //code adapted from the example on studres: https://studres.cs.st-andrews.ac.uk/CS1006/Examples/NCServer.java
            System.out.println("Broadcasting server on " + InetAddress.getLocalHost().toString() + ":10006");

            //Waits of incoming connection from clients
            ServerSocket server = new ServerSocket(10006);
            System.out.println("Waiting for client connection");

            //first client connection
            client = server.accept();

            if (gameType.equals("AI")) {
                this.isAI = true;
                server.setSoTimeout(5000);
            } else {
                this.isAI = false;
                server.setSoTimeout(30000);
            }

            System.out.println("Client 1 connection accepted");
            // Don't accept any more connections
            server.close();

            //reading from client 1
            clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));

            //sending to client 2
            serverOutput = new PrintWriter(client.getOutputStream(), true);

            //initialise game request/game play
            initGamePlay();

            //close the socket after the game ends
            clientInput.close();
            serverOutput.close();
            client.close();
            System.out.println("Server shutting down");

        } catch (SocketException e) {
            System.out.println("Conneciton time out");
        } catch (IOException e){
            System.out.println("IO Error");
        } catch (NullPointerException e) {
            System.out.println("Client did not respond");
        }
    }

    //method for gameplay on the network
    private void runGame() {

        int resendCounter = 0;
        
        //game loop
        do {
            try {

                //server plays white
                if(counterColour.equals("White")) {
                    if (isAI) {
                        //AI playing
                        AI ai = new AI(board, counterColour);
                        String reply = networkUtilities.makeMoveAI(ai, board, counterColour);
                        sendReply(reply);
                    } else {
                        //human playing
                        String reply = networkUtilities.makeMoveHuman(board, counterColour);
                        sendReply(reply);
                    }

                //client's move
                } else {

                    command = clientInput.readLine();

                    //client sends resend
                    if (command.equals("resend")) {
                        serverOutput.println(reply);
                        serverOutput.flush();

                    //client sends win message
                    } else if (command.equals("You win")) {
                        System.out.println("White: " + board.getWhiteCount() + "     Black: " + board.getBlackCount());
                        System.out.println("Game over, server wins");
                        command = "bye";

                    //client sends bye message
                    } else if (command.equals("bye")) {
                        command = "bye";

                    //client sends pass message
                    } else if (command.equals("move pass")) {
                        counterColour = board.altCounterColour(counterColour);

                    //verify client wins
                    } else if (command.equals("I win")) {
                        System.out.println("White: " + board.getWhiteCount() + "     Black: " + board.getBlackCount());
                        if (board.getBlackCount() > board.getWhiteCount()) {
                            System.out.println("Game over, client wins");
                            command = "bye";
                        } else {
                            System.out.println("Black counters are not greater than white counters, please review the board");
                            board.display();
                            command = "bye";
                        }

                    //tie game
                    } else if (command.equals("we tie")) {
                        command = "bye";

                    //client sends 'move' eg 'move e3' or other
                    } else {

                        try {
                            //obtain co ords of client move
                            int[] coOrds = networkUtilities.translateInput(command);
                            String message = networkUtilities.translateOutput(coOrds);
                            //check if move is legal
                            board.initLegalMoves(counterColour);
                            if (board.isLegalMove(coOrds[0], coOrds[1])) {
                                //output move and update the board
                                System.out.println("Client moved: " + message);
                                board.makeMove(coOrds, counterColour);

                                resendCounter = 0; //reset the counter after every legal move
                                counterColour = board.altCounterColour(counterColour);

                            //client sends an illegal move
                            } else {
                                serverOutput.println("I win");
                                serverOutput.flush();
                                command = "bye";
                            }

                        //client sends a move the server doesn't understand
                        } catch (MoveNotFoundException e ) {
                            //client can't send more than 2 messages the server does not understand
                            if (resendCounter < 2) {
                                serverOutput.println("resend");
                                serverOutput.flush();
                                resendCounter++;
                            } else {
                                System.out.println("Client sent 2 commands in succession which do not make sense, server wins by default");
                                serverOutput.println("I win");
                                serverOutput.flush();
                                command = "bye";
                            }

                        }
                    }
                }

            } catch (IOException e) {
                System.out.println("Client took too long to respond");
                System.out.println("Server wins by default");
                //close the connection
                // clientInput.close();
                // serverOutput.close();
                // client.close();
                // System.out.println("Server shutting down");
            } catch (MoveNotFoundException e) {
                System.out.println("An error occurred with the server");
            }

        } while (!command.equals("bye"));
    }

    //initialising game play over the network
    private void initGamePlay() throws IOException, SocketException {

        //connection loop
        do {
            reply = clientInput.readLine();

            //client sends a hello message
            if (reply.equals("hello")) {
                System.out.print("Hello message received from " + client.getInetAddress().getHostName() + ": ");
                command = sc.nextLine();
                serverOutput.println(command);
                serverOutput.flush();

            //client sends new game message
            } else if (reply.equals("new game")) {
                System.out.print("New game request received from " + client.getInetAddress().getHostName() + ", accept? ");
                command = sc.nextLine();

                //accpet the new game
                if (command.equals("accept")) {
                    serverOutput.println(command);
                    serverOutput.flush();
                    //start game
                    runGame();
                    command = "reject";

                //reject the new game
                } else {
                    command = "reject";
                    serverOutput.println(command);
                    serverOutput.flush();
                }

            //if the client sends a message that isn't hello or new game
            } else {
                System.out.print("Message '" + reply + "' received from " + client.getInetAddress().getHostName() + ": ");
                command = sc.nextLine();
                serverOutput.println(command);
                serverOutput.flush();
            }

        } while(!command.equals("reject"));

    }

    //send reply to the client
    private void sendReply(String reply) throws MoveNotFoundException{

        //white win
        if (reply.equals("White win")) {
                reply = "I win";
                command = "bye";
                serverOutput.println(reply);
                serverOutput.flush();
        //black win
        } else if (reply.equals("Black win")) {
            reply = "You win";
            command = "bye";
            serverOutput.println(reply);
            serverOutput.flush();

        //move pass
        } else if (reply.equals("move pass")) {
            serverOutput.println(reply);
            serverOutput.flush();
            counterColour = board.altCounterColour(counterColour);

        //tie game
        } else if (reply.equals("we tie")) {
            command = "bye";
            serverOutput.println("we tie");
            serverOutput.flush();

        } else if (reply.equals("You win")) {
            command = "bye";
            serverOutput.println(reply);
            serverOutput.flush();

        //move
        } else {
            int coOrds[] = networkUtilities.translateInput(reply);
            System.out.println("Server moved: " + networkUtilities.translateOutput(coOrds));
            board.makeMove(coOrds, counterColour);
            counterColour = board.altCounterColour(counterColour);
            serverOutput.println(reply);
            serverOutput.flush();
        }
    }
}
