/**
 * Othello/Reversi is a game for 2 players
 * The objective of the game is whoever has the most counters
 * of their colour on the board, they win.
 * Counters must be placed in such a way that they 'flip' the 
 * opponents counter, failing to do so is an illegal move
 */

public class Main {

    /**
     * Main method
     */
    public static void main(String[] args) {

        //run the MainFrame GUI on a thread safe environment
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               MainFrame mf = new MainFrame();
            }
        });

    }
}