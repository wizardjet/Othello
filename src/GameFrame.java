/**
 * This class acts as a container frame for the Game Panel, Score Panel
 * and quit button.
 * It implements the ActionListener interface to determine if the user presses 
 * the quit button to close the frame.
 */

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GameFrame extends JFrame implements ActionListener {
    
    private final int GUI_WIDTH = 1200;
    private final int GUI_HEIGHT = 900;
    private final int PANEL_WIDTH = 640;
    private final int PANEL_HEIGHT = 640;

    private JButton quitButton;

    /**
     * Constructor for the game frame determines how many players are
     * involved in the game.
     * @param singlePlayer - whether the game is for a single player or multiplayer
     * @param noOfPlayers - number of physical players
     */
    public GameFrame(boolean singlePlayer, int noOfPlayers) {
        //create and setup the frame
        setTitle("Othello");
        setSize(GUI_WIDTH, GUI_HEIGHT);
        getContentPane().setBackground(Color.WHITE);

        //create the singleplayer GUI if the user selects singleplayer
        if (singlePlayer) {
            createSinglePlayerGUI();
        //create the multiplayer GUI if the user selects multiplayer
        } else if (noOfPlayers == 1) {
            createLocalMultiplayerGUI();
        //create network multiplayer GUI
        } else {
            createMultiPlayerGUI();
        }
    }

    /**
     * This method creates the mutliplayer GUI with input boxes determining 
     * whether the user wants to be the client or server.
     */
    private void createMultiPlayerGUI() {
        //possibilities to choose from
        String[] networkPossibilities = {"Server", "Client"};
        String[] gamePossibilities = {"AI", "Human"};

        //determine whether the user wants to be the client or server
        String networkType = (String)JOptionPane.showInputDialog(null, "Choose network type", "Choose network", JOptionPane.PLAIN_MESSAGE, null, networkPossibilities, "");
        
        //user clicks cancel on the JOptionPane
        if (networkType == null) {
            return;

        } else {
            //determine whether the user wants to play with AI or as themselves
            String gameType = (String)JOptionPane.showInputDialog(null, "Choose game type", "Choose game", JOptionPane.PLAIN_MESSAGE, null, gamePossibilities, "");
            
            //user clicks cancel on the JOptionPane
            if (gameType == null) {
                return;

            //launch server or client object depending on the user's choices
            } else {
                if (networkType.equals("Server")) {
                    //this.dispose(); //GUI For multiplayer would go here
                    Board board = new Board(8);
                    OthelloServer os = new OthelloServer(gameType, board);
                    createMultiPlayerGUI(board);
                } else {
                    //this.dispose(); //GUI For multiplayer would go here
                    Board board = new Board(8);
                    OthelloClient oc = new OthelloClient(gameType, board);
                    createMultiPlayerGUI(board);
                }
            }
        }
    }

    /**
     * This method creates the single player GUI with the player getting the
     * choice to play black or white. 
     * The GamePanel, ScorePanel and quit button are added to the frame
     */
    private void createSinglePlayerGUI() {
        //counter colour possibilities for the user to choose
        String[] possibilities = {"Black", "White"};
        String counterColour = (String)JOptionPane.showInputDialog(null, "Choose colour to play", "Choose colour", JOptionPane.PLAIN_MESSAGE, null, possibilities, "Black");
        
        //user clicks cancel on the JOptionPane
        if (counterColour == null) {
            return;
        } else {

            //game panel
            GamePanel gp = new GamePanel(true, false, counterColour);
            gp.setSize(PANEL_WIDTH, PANEL_HEIGHT);

            //score panel
            ScorePanel sp = new ScorePanel(gp.getBoard());

            //quit button
            quitButton = new JButton("Quit");
            quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            quitButton.addActionListener(this);
            
            //jpanel to add score panel and quit button
            JPanel panel = new JPanel();
            panel.setBackground(Color.WHITE);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(30,30,30,30));
            panel.add(sp);
            panel.add(quitButton);

            //use box to add all components
            Box box = Box.createHorizontalBox();
            box.add(gp);
            box.add(panel);

            //add box to jframe
            add(box);

            //display jframe
            pack();
            setResizable(false);
            setVisible(true);
        }
    }

    /**
     * This method creates the local multiplayer GUI
     * The GamePanel, ScorePanel and quit button are added to the frame
     */
    private void createLocalMultiplayerGUI() {
        //initialising GamePanel and ScorePanel
        GamePanel gp = new GamePanel(false, false, "Black");
        ScorePanel sp = new ScorePanel(gp.getBoard());

        //new Quit JButton
        quitButton = new JButton("Quit");
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.addActionListener(this);
        gp.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        
        //jpanel to add score panel and quit button
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30,30,30,30));
        panel.add(sp);
        panel.add(quitButton);

        //use box to add all components
        Box box = Box.createHorizontalBox();
        box.add(gp);
        box.add(panel);

        //add box to jpanel
        add(box);

        //display jpanel
        pack();
        setResizable(false);
        setVisible(true);
    }

    private void createMultiPlayerGUI(Board board) {
        //initialising GamePanel and ScorePanel
        GamePanel gp = new GamePanel(false, false, "Black");
        gp.setBoard(board);
        ScorePanel sp = new ScorePanel(board);
         //new Quit JButton
        quitButton = new JButton("Quit");
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.addActionListener(this);
        gp.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        
        //jpanel to add score panel and quit button
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(30,30,30,30));
        panel.add(sp);
        panel.add(quitButton);

        //use box to add all components
        Box box = Box.createHorizontalBox();
        box.add(gp);
        box.add(panel);

        //add box to jpanel
        add(box);

        //display jpanel
        pack();
        setResizable(false);
        setVisible(true);
    }

    /**
     * This method disposes the frame if the user clicks the
     * quit button
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == quitButton) {
            this.dispose();
        }
    }
}
