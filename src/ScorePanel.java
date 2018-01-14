/**
 * This class displays the scores of the current game to the user
 * This class implements the Observer interface to observe changes in the board
 */

import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

public class ScorePanel extends JPanel implements Observer {

    private final int PANEL_HEIGHT = 300;
    private final int PANEL_WIDTH = 300;
    private final int TEXT_FONT_SIZE = 20;
    private final int SCORE_FONT_SIZE = 28;

    private Board board;
    private int noOfWhiteCounters;
    private int noOfBlackCounters;
    private JLabel whiteCounterText;
    private JLabel blackCounterText;
    private JLabel whiteCounterScore;
    private JLabel blackCounterScore;


    /**
     * Constructor for the the ScorePanel
     */
    public ScorePanel(Board board) {
        //set the variables of the scorepanel, with the initial scores of the board
        this.board = board;
        board.addObserver(this);
        this.noOfBlackCounters = board.getBlackCount();
        this.noOfWhiteCounters = board.getWhiteCount();

        //create the GUI
        createPanel();

        //set display variabels of the GUI
        setBackground(Color.WHITE);
        setSize(PANEL_WIDTH, PANEL_HEIGHT);
        setVisible(true);
    }

    /**
     * This method is triggered when the conditions/score of the board
     * changes.
     */
    @Override
    public void update(Observable observable, Object arg) {
        board = (Board) observable;
        noOfWhiteCounters = board.getWhiteCount();
        noOfBlackCounters = board.getBlackCount();
        //update the panel so that the scores reflect the current condition
        updatePanel();
    }

    /**
     * This method creates the GUI of the ScorePanel
     */
    private void createPanel() {
        //Black counter text
        blackCounterText = new JLabel("Black Score: ");
        blackCounterText.setFont(new Font("Imprint MT Shadow", Font.PLAIN, TEXT_FONT_SIZE));
        blackCounterText.setForeground(Color.black);

        //White counter text
        whiteCounterText = new JLabel("White Score: ");
        whiteCounterText.setFont(new Font("Imprint MT Shadow", Font.PLAIN, TEXT_FONT_SIZE));
        whiteCounterText.setForeground(Color.black);

        //Black counter score
        blackCounterScore = new JLabel(Integer.toString(noOfBlackCounters));
        blackCounterScore.setFont(new Font("Imprint MT Shadow", Font.PLAIN, SCORE_FONT_SIZE));
        blackCounterScore.setForeground(Color.black);

        //White counter score
        whiteCounterScore = new JLabel(Integer.toString(noOfWhiteCounters));
        whiteCounterScore.setFont(new Font("Imprint MT Shadow", Font.PLAIN, SCORE_FONT_SIZE));
        whiteCounterScore.setForeground(Color.black);

        //create box to put labels on
        Box box = Box.createVerticalBox();
        box.setOpaque(true);
        box.setBackground(Color.WHITE);

        //add components to box
        box.add(blackCounterText);
        box.add(blackCounterScore);
        box.createVerticalStrut(30);
        box.add(whiteCounterText);
        box.add(whiteCounterScore);

        add(box);
    }

    /**
     * This method updates the panel by recreating the panel
     */
    private void updatePanel() {
        this.removeAll();
        createPanel();
        this.revalidate();
        this.repaint();
    }

}