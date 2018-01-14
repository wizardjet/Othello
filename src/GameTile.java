/**
 * This class represents the different tiles on the GamePanel class
 */

import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class GameTile extends JLabel {

    //position of the game tile
    private int[] position;

    //image files for the different scenarios
    private final ImageIcon emptyIcon = new ImageIcon("emptytile.png");
    private final ImageIcon blackIcon = new ImageIcon("blacktile.png");
    private final ImageIcon whiteIcon = new ImageIcon("whitetile.png");
    private final ImageIcon legalMoveIcon =  new ImageIcon("legalmovetile.png");

    /**
     * Constructor for the GameTile
     * @param position of the Game Tile
     */
    public GameTile(int[] position) {
        //store the position
        this.position = position;
        //set the text of the JLabel to be empty
        setText("");
    }

    //set the image icon of the game tile to a legal move tile image
    public void setLegal() {
        this.setIcon(legalMoveIcon);
    }

    //set the image icon of the game tile to an empty tile image
    public void setEmpty() {
        this.setIcon(emptyIcon);
    }

    //set the image icon of the game tile to a black tile image
    public void setBlack() {
        this.setIcon(blackIcon);
    }

    //set the image icon of the game tile to a white tile image
    public void setWhite() {
        this.setIcon(whiteIcon);
    }

    //method to get the position of the game tile
    public int[] getPosition() {
        return position;
    }
}